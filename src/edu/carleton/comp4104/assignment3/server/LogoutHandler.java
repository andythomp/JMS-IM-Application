package edu.carleton.comp4104.assignment3.server;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.io.IOException;

import javax.jms.JMSException;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.MessageBundle;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.EventHandler;

/**
 * Server's Logout Handler
 * When it receives a logout event, it broad casts the logout message
 * to all available connections.
 * @author Andrew Thompson
 *
 */
public class LogoutHandler implements EventHandler {

	/**
	 * @param event
	 * @throws IOException
	 * @author Andrew Thompson
	 */
	@Override
	public void handleEvent(Event event) throws IOException {
		LoggingManager.logln("Server Logout Handler Called.");
		
		/*Should see two items in the event, reference back to the local network manager
		 * and a message bundle that we received.
		 */
		ServerNetworkManager networkManager = (ServerNetworkManager) event.getResource(Event.NETWORK_MANAGER);
		MessageBundle bundle = (MessageBundle) event.getResource(Event.BUNDLE);
		
		/*
		 * Rest of the resources should be here in the bundle.
		 */
		String userName = (String) bundle.getResource(MessageBundle.ORIGIN_CLIENT);
		
		if (networkManager == null){
			LoggingManager.logerr("Logout Received but networkManager was null.");
			return;
		}
		if (userName == null){
			LoggingManager.logerr("Logout Received but userName was null.");
			return;
		}
		
		LoggingManager.logln("User: " + userName + " has logged out.");
		
		try {
			networkManager.broadCastMessage(bundle);
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
		}
	}

}