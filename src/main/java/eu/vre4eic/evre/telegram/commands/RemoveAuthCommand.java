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


import org.bson.Document;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * This commands delete the Telegram account as authenticator
 *
 */
public class RemoveAuthCommand extends BotCommand {

	public static final String LOGTAG = "STARTCOMMAND";

	public RemoveAuthCommand() {
		super("remove", "This command deletes this Telegram Id as authenticator for <i>username</i> in e-VRE, usage: <i>/remove username password</i>");
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {


		String userName = user.getFirstName() + " " + user.getLastName();
		StringBuilder messageBuilder = new StringBuilder();
		if (arguments.length!=2){
			messageBuilder.append("Hi ").append(userName).append("\n");
			messageBuilder.append("please use: /remove username pwd");
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
				//System.out.println("################## "+cursor.next());
				Document userCan=cursor.next();
				String pwd=userCan.getString("password");
				if(pwd.equals(arguments[1])){

					String aId=userCan.getString("authId");
					if (!aId.equals("0")){
						// we don't check if the chat.getId() is the same,
						//because a user can remove this from another Telegram ID,
						// need to check this
						
						userCan.replace("authId", "0");
						BasicDBObject updateObj = new BasicDBObject();
						updateObj.put("$set", userCan);
						//check this!!!
						collection.updateOne(searchQuery, updateObj);

						messageBuilder.append("Done ").append(userName).append(", \n");
						messageBuilder.append("this Telegram account is no longer an e-VRE Authenticator for "+arguments[0]);
					}
					else{//the user with the provided credentials has no authenticator defined
						messageBuilder.append("Hi ").append(userName).append(",\n");
						messageBuilder.append("something went wrong, please contact the administrator!");
					}

				}else {//error credentials wrong
					messageBuilder.append("Hi ").append(userName).append("\n");
					messageBuilder.append("credentials not valid!");
				}

			}else {//error credentials wrong
				messageBuilder.append("Hi ").append(userName).append(",\n");
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
}