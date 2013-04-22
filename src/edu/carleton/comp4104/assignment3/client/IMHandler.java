package edu.carleton.comp4104.assignment3.client;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This event handler gets called when ever a client receives an instant message.
 * It should tell the controller to write the message to the user for whom it is received.
 */

import java.io.IOException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.MessageBundle;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.EventHandler;


public class IMHandler implements EventHandler {
	
	/**
	 * Takes an instant message event and gives it to the controller to
	 * post to the GUI.
	 * @param event - Event to be Handled
	 * @throws IOException
	 * @author Andrew Thompson
	 */
	@Override
	public void handleEvent(Event event) throws IOException {
		LoggingManager.logln("IM handler called.");
		
		/*Should see two items in the event, reference back to the local network manager
		 * and a message bundle that we received.
		 */
		ChatController controller = (ChatController) event.getResource(Event.CONTROLLER);
		MessageBundle bundle = (MessageBundle) event.getResource(Event.BUNDLE);
		
		
		
		String userName = (String) bundle.getResource(MessageBundle.ORIGIN_CLIENT);
		String message = (String) bundle.getResource(MessageBundle.MESSAGE);
		if (message == null){
			LoggingManager.logerr("Error: IM Received but message was null.");
			return;
		}
		if (controller == null){
			LoggingManager.logerr("Error: IM Received but controller was null.");
			return;
		}
		if (userName == null){
			LoggingManager.logerr("Error: IM Received but userName was null.");
			return;
		}
		controller.addMessage(message, userName);
		
	}
}