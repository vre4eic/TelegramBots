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
package eu.vre4eic.evre.telegram.updateshandlers;

//import org.slf4j.Logger;

import java.util.logging.Level;

import eu.vre4eic.evre.core.comm.Publisher;
import eu.vre4eic.evre.core.comm.Subscriber;
import eu.vre4eic.evre.core.comm.SubscriberFactory;
import eu.vre4eic.evre.core.messages.AuthenticationMessage;
import eu.vre4eic.evre.core.messages.MultiFactorMessage;
import eu.vre4eic.evre.telegram.commands.HelloCommand;
import eu.vre4eic.evre.telegram.commands.HelpCommand;
import eu.vre4eic.evre.telegram.commands.RegisterAuthCommand;
import eu.vre4eic.evre.telegram.commands.RemoveAuthCommand;
import eu.vre4eic.evre.telegram.modules.TGBotMFAListener;

import org.telegram.services.Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.logging.BotLogger;


public class TgAuthenticator extends  TelegramLongPollingCommandBot {
	
	 public static final String LOGTAG = "AUTHENTICATIONHANDLER";
	 Publisher<AuthenticationMessage> ap;
	//private Logger log = LoggerFactory.getLogger(this.getClass());

	 /**
	     * Constructor.
	     */
	    public TgAuthenticator() {
	    	 BotLogger.setLevel(Level.INFO);
	        register(new HelloCommand());
	        register(new RegisterAuthCommand());
	        register(new RemoveAuthCommand());
	        HelpCommand helpCommand = new HelpCommand(this);
	        register(helpCommand);

	        registerDefaultAction((absSender, message) -> {
	            SendMessage commandUnknownMessage = new SendMessage();
	            commandUnknownMessage.setChatId(message.getChatId());
	            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by e-VRE bot. Here comes some help " + Emoji.AMBULANCE);
	            try {
	                absSender.sendMessage(commandUnknownMessage);
	            } catch (TelegramApiException e) {
	                BotLogger.error(LOGTAG, e);
	            }
	            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[] {});
	        });
	        Subscriber<MultiFactorMessage> subscriber = SubscriberFactory.getMFASubscriber();
			subscriber.setListener(new TGBotMFAListener(this));
	    }
    @Override
    public void processNonCommandUpdate(Update update)  {
    	
    	
    	 if (update.hasMessage() && update.getMessage().hasText()) {
    	        SendMessage message = new SendMessage() 
    	                .setChatId(update.getMessage().getChatId())
    	                .setText(update.getMessage().getText());
    	        try {
    	        	
    	        	System.out.println("************************** ");
    	        	System.out.println(" ======= "+message.getChatId());
    	        	System.out.println(" ======= "+message.getText());  
    	        	System.out.println("************************** ");
    	            sendMessage(message); // Call method to send the message
    	        } catch (TelegramApiException e) {
    	            e.printStackTrace();
    	            System.out.println(e.toString());
    	        }
    	    }
    }
    
    public void send2faCode(int chatId, String code){
    	 SendMessage sendMessage = new SendMessage();
         sendMessage.enableMarkdown(true);
         sendMessage.setChatId(String.valueOf(chatId));
         sendMessage.setText(code);
         try {
             sendMessage(sendMessage);
         } catch (TelegramApiRequestException e) {
             BotLogger.warn(LOGTAG, e);
             if (e.getApiResponse().contains("Can't access the chat") || e.getApiResponse().contains("Bot was blocked by the user")) {
            	 BotLogger.warn(LOGTAG, e.getApiResponse());
             }
         } catch (Exception e) {
             BotLogger.severe(LOGTAG, e);
         }
    }
   
    @Override
    public String getBotUsername() {
        
        return "evre_tg_auth_bot";
    }

    @Override
    public String getBotToken() {
        
        return "304076530:AAFryi3D0xd7e6Vu_yyfiOpIMAPiHDAwIq0";
    }
	

	
}
