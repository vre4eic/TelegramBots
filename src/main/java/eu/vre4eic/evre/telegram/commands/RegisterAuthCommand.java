/*******************************************************************************
 * Copyright (c) 2017 VRE4EIC Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package eu.vre4eic.evre.telegram.commands;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.bson.types.Binary;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

//import eu.vre4eic.evre.SpringMongoConfig;
import eu.vre4eic.evre.core.impl.EVREUserProfile;
import eu.vre4eic.evre.nodeservice.usermanager.UserManager;
import eu.vre4eic.evre.nodeservice.usermanager.dao.UserProfileRepository;
import eu.vre4eic.evre.nodeservice.usermanager.impl.UserManagerImpl;

/**
 * This commands registers the Telegram account as authenticator
 *
 */

public class RegisterAuthCommand extends BotCommand {

	public static final String LOGTAG = "STARTCOMMAND";

	
	//private UserManager userManager= new UserManagerImpl();
	

	public RegisterAuthCommand() {
		super("register", "This command registers this Telegram Id  as an authenticator for a user in e-VRE, usage: <i>/register username password</i>");
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

		
		String userName = user.getFirstName() + " " + user.getLastName();
		StringBuilder messageBuilder = new StringBuilder();
		if (arguments.length!=2){
			messageBuilder.append("Hi ").append(userName).append("\n");
			messageBuilder.append("please use: /register username pwd");
		}
		else{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase db = mongoClient.getDatabase("evre");
			MongoCollection<Document> collection=db.getCollection("eVREUserProfile");
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("userId", arguments[0]);
			FindIterable<Document> itcursor=collection.find(searchQuery);
			//FindIterable<Document> itcursor=collection.find();
			MongoCursor<Document> cursor=itcursor.iterator();
			if (cursor.hasNext()){
				
				Document userCan=cursor.next();
				String pwd=userCan.getString("password");
				
				Binary binS = userCan.get("salt", org.bson.types.Binary.class);
				String salt= new String(binS.getData());
				boolean validUser=false;
				if(pwd.equals(arguments[1]))
					validUser=true;
				
				if (salt != null && !checkEncryptedData(arguments[1], salt.getBytes()).equals(arguments[1]))
					validUser=true;
				
				//if(pwd.equals(arguments[1])){
				if(validUser){
					
					userCan.replace("authId", chat.getId());
					BasicDBObject updateObj = new BasicDBObject();
					updateObj.put("$set", userCan);
					//check this!!!
					collection.updateOne(searchQuery, updateObj);
					
					messageBuilder.append("Done ").append(userName).append(",\n");
					messageBuilder.append("this Telegram account is now registered as e-VRE Authenticator for "+arguments[0]);
					
				}else {//error credentials wrong
					messageBuilder.append("Hi ").append(userName).append("\n");
					messageBuilder.append("credentials not valid!");
				}
				
			}else {//error credentials wrong
				messageBuilder.append("Hi ").append(userName).append("\n");
				messageBuilder.append("credentials not valid!");
			}
			mongoClient.close();
		}

		SendMessage answer = new SendMessage();
		answer.setChatId(chat.getId().toString());
		answer.setText(messageBuilder.toString());

		try {
			absSender.sendMessage(answer);
		} catch (TelegramApiException e) {
			BotLogger.error(LOGTAG, e);
		}
		
	}
	private String checkEncryptedData(String data, byte[] salt){
		byte[] secret=new byte[12];
		String passHash=data;
		
		  String uPwd=data;
		  try {
			 PBEKeySpec keySpec = new PBEKeySpec(uPwd.toCharArray(), salt, 2048, 160);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			
			secret = keyFactory.generateSecret(keySpec).getEncoded();
			

			passHash = Base64.encodeBase64String(secret);
	       // String saltString = Base64.encodeBase64String(salt);
			System.out.println("data " + uPwd+", secret "+passHash);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return passHash;
	}
}