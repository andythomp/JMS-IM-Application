package edu.carleton.comp4104.assignment3.client;

import java.io.IOException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.MessageBundle;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.EventHandler;

/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This event handler is called when ever a user logs out from
 * the server. It should remove the user from the user list in the controller,
 * and have the controller update the GUI.
 * 
 */
public class LogoutHandler implements EventHandler{

	@Override
	public void handleEvent(Event event) throws IOException {
		LoggingManager.logln("Logout handler called.");
		/*Should see two items in the event, reference back to the local network manager
		 * and a message bundle that we received.
		 */
		ChatController controller = (ChatController) event.getResource(Event.CONTROLLER);
		MessageBundle bundle = (MessageBundle) event.getResource(Event.BUNDLE);
		
		/*
		 * Rest of the resources should be here in the bundle.
		 */
		String userName = (String) bundle.getResource(MessageBundle.ORIGIN_CLIENT);
		LoggingManager.logln("Logging out user:" + userName);
		
		//Make sure nothing is null
		if (controller == null){
			LoggingManager.logerr("Logout Received but controller was null.");
			return;
		}
		if (userName == null){
			LoggingManager.logerr("Logout Received but userName was null.");
			return;
		}
		//We can't log out ourselves...
		if (userName.equals(controller.getUserName())){
			
		}
		//Ok remove that user
		else{
			controller.removeUser(userName);
		}
		
	}


}
