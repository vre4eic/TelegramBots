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
package eu.vre4eic.evre.telegram.modules;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vre4eic.evre.core.messages.AuthenticationMessage;
import eu.vre4eic.evre.core.messages.MultiFactorMessage;
import eu.vre4eic.evre.core.comm.MessageListener;
import eu.vre4eic.evre.telegram.updateshandlers.TgAuthenticator;

/**
 * 
 * Class used to receive asynchronous messages related to users authenticated by  the system.
 * The authentication message can represent  Login or Logout operations and contains token which must be used by users for each service invocation.
 * @author francesco
 *
 */
public class TGBotMFAListener implements MessageListener<MultiFactorMessage>{
	
	private TgAuthenticator module;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private Vector<String > authTable;
	
	public  TGBotMFAListener(TgAuthenticator authModule) {
		this.module = authModule;
	}
	
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(MultiFactorMessage message) {

		log.info("##### MultiFactorMessage message arrived #####");
		module.send2faCode(new Integer(message.getAuthId()), "The code to authenticate '"+message.getUserId()+"' in e-VRE is: " + message.getCode());     

	}



}
