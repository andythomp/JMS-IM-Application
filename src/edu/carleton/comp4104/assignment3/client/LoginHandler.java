package edu.carleton.comp4104.assignment3.client;

import java.io.IOException;

import javax.jms.JMSException;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.MessageBundle;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.EventHandler;
import edu.carleton.comp4104.assignment3.rmi.TransferEngine;

/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This event handler is called when ever a client receives a login message
 * from the server. It will add that client to it's client list, and then send
 * a handshake to the new client.
 * 
 * @author Andrew Thompson
 */
public class LoginHandler implements EventHandler{
	
	/**
	 * SUCCESS AND FAILURE CONSTANTS
	 */
	//private static final String SUCCESS = "SUCCESS";
	//private static final String FAILURE = "FAILURE";
	
	/**
	 * Takes a login event and handles it based on criteria (see class description)
	 * @param event - Event to be handled
	 * @throws IOException
	 * @author Andrew Thompson
	 */
	@Override
	public void handleEvent(Event event) throws IOException {
		LoggingManager.logln("Login handler called.");
		
		/*Should see two items in the event, reference back to the local network manager
		 * and a message bundle that we received.
		 */
		ChatController controller = (ChatController) event.getResource(Event.CONTROLLER);
		MessageBundle bundle = (MessageBundle) event.getResource(Event.BUNDLE);
		
		/*
		 * Rest of the resources should be here in the bundle.
		 */
		TransferEngine fileService = (TransferEngine) bundle.getResource(MessageBundle.FILE_SERVICE);
		String userName = (String) bundle.getResource(MessageBundle.ORIGIN_CLIENT);
		String networkService = (String) bundle.getResource(MessageBundle.NETWORK_SERVICE);
		
		//Make sure we got everything we need.
		if (controller == null){
			LoggingManager.logerr("Login Received but controller was null.");
			return;
		}
		if (fileService == null){
			LoggingManager.logerr("Login Received but fileService was null.");
			return;
		}
		if (userName == null){
			LoggingManager.logerr("Login Received but userName was null.");
			return;
		}
		if(networkService == null){
			LoggingManager.logerr("Login Received but networkService was null.");
			return;
		}
			
		
		// That means we got our own loging message back, so initialize the GUI
		if (userName.equals(controller.getUserName())){
			controller.initializeGUI(userName);
			
		}
		//Someone else must have logged in, lets add them to the user list.
		else{
			Client client = new Client(userName, fileService);
			controller.addClient(client);
		
			try {
				//Now we tell that new client that we exist
				controller.getNetworkManager().addPublisher(userName, networkService);
				controller.sendClientHandshake(userName);
			} catch (NamingException | JMSException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		
	}
	
}
