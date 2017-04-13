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
        super("remove", "This command deletes this Telegram account as authenticator for <i>username</i> in e-VRE, usage: <i>/removea username password</i>");
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
					
					userCan.replace("authId", "0");
					BasicDBObject updateObj = new BasicDBObject();
					updateObj.put("$set", userCan);
					//check this!!!
					collection.updateOne(searchQuery, updateObj);
					
					messageBuilder.append("Done ").append(userName).append(", \n");
					messageBuilder.append("this Telegram account is no longer an e-VRE Authenticator for "+arguments[0]);
					
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
}