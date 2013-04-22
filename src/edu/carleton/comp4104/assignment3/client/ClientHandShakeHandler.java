package edu.carleton.comp4104.assignment3.client;

/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * When a client receives a log in message, it sends the client who logged in
 * a hand shake message, telling that client that it exists and wants to communicate
 * with them. This is the handler for a handshake
 */

import java.io.IOException;

import javax.jms.JMSException;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.MessageBundle;
import edu.carleton.comp4104.assignment3.jms.ServerList;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.EventHandler;
import edu.carleton.comp4104.assignment3.rmi.TransferEngine;

public class ClientHandShakeHandler implements EventHandler {

	/**
	 * @param event
	 * @throws IOException
	 * @author Andrew Thompson
	 */
	@Override
	public void handleEvent(Event event) throws IOException {
		LoggingManager.logln("Client Handshake Handler called.");
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
		//Check to make sure we have all the information we need
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
			
		
		//If we are getting a handshake from ourselves do nothing
		if (userName.equals(controller.getUserName())){
			
		}
		else{
			//Try and add them to our client list
			try {
				controller.getNetworkManager().addPublisher(userName, networkService);
				Client client = new Client(userName, fileService);
				controller.addClient(client);
			} 
			//If that fails, then try again with the next server and work with the failover
			catch (NamingException | JMSException e) {
				try {
					controller.getNetworkManager().addPublisher(userName, ServerList.getNextServer(networkService));
					Client client = new Client(userName, fileService);
					controller.addClient(client);
				} catch (NamingException | JMSException e1) {
					e1.printStackTrace();
				}
				
			}
			
		}
	}

}
