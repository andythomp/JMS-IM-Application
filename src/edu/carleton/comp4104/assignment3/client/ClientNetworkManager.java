package edu.carleton.comp4104.assignment3.client;

/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * Client's implementation of a network manager. Know's how to send the various messages that 
 * a client would need to send to the outside world.
 */

import javax.jms.JMSException;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.MessageBundle;
import edu.carleton.comp4104.assignment3.jms.NetworkManager;
import edu.carleton.comp4104.assignment3.jms.ServerList;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.rmi.Transfer;

public class ClientNetworkManager extends NetworkManager{

	private String serverName;
	private boolean initialized = false;
	public String getServerName() {
		return serverName;
	}


	private ChatController controller;
	
	/**
	 * Creates a new Network Manager with a handle back to the chat controller.
	 * @param controller - Handle back to the controller
	 * @throws NamingException
	 * @throws JMSException
	 * @author Andrew Thompson
	 */
	public ClientNetworkManager(ChatController controller) throws NamingException, JMSException{
		super();
		this.controller = controller;
		//Randomly select a server
		serverName = ServerList.getRandomServer();

		//First we will add a publisher to write to the server we randomized
		/***************************************************************************/
		LoggingManager.logln("ClientNetworkManager: Connecting to Server:" + serverName);
		try{
			addPublisher(serverName, serverName);
		}
		//Check for failover
		catch(Exception e){
			LoggingManager.logerr(serverName + " is not available. Connecting to the next server.");
			serverName = ServerList.getNextServer(serverName);
			addPublisher(serverName, serverName);
		}

		//Now add a subscriber to listen to messages the server sends us about clients.
		/***************************************************************************/
		LoggingManager.logln("Adding subscriber for: " + CLIENTS);
		try{
			addSubscriber(CLIENTS, serverName);
		}
		//Check for failover
		catch(Exception e){
			LoggingManager.logerr(serverName + " is not available. Connecting to the next server.");
			
			addSubscriber(CLIENTS, serverName);
		}
		
		//Now add a subscriber to listen to messages about our selves
		/***************************************************************************/
		LoggingManager.logln("Adding subscriber for: " + controller.getUserName());
		try{
			addSubscriber(controller.getUserName(), serverName);
		}
		//Check for failover
		catch(Exception e){
			LoggingManager.logerr(serverName + " is not available. Connecting to the next server.");
			addSubscriber(controller.getUserName(), serverName);
		}
		setInitialized(true);
	}
	
	/**
	 * Sends a login message to the server, requesting that their presence
	 * be broadcasted to the other clients on the server network.
	 * @param originClient - Name of the client the login is originating from
	 * @param fileService - Handle to the active client's RMI file service
	 * @throws JMSException
	 * @throws NamingException
	 * @author Andrew Thompson
	 */
	public void sendLoginMessage(String originClient, Transfer fileService) throws JMSException, NamingException{
		while (!isInitialized()){
			
		}
		MessageBundle bundle = new MessageBundle(MessageBundle.LOGIN);
		bundle.addContent(MessageBundle.ORIGIN_CLIENT, originClient);
		bundle.addContent(MessageBundle.FILE_SERVICE,  fileService);
		bundle.addContent(MessageBundle.NETWORK_SERVICE, serverName);
		sendMessage(serverName, bundle);
	}
	
	/**
	 * Sends a logout message to the server, requesting that their presence
	 * be broadcasted to the other clients on the server network.
	 * @param originClient - Name of the client the login is originating from
	 * @throws JMSException
	 * @throws NamingException
	 * @author Andrew Thompson
	 */
	public void sendLogoutMessage(String originClient) throws JMSException, NamingException{
		MessageBundle bundle = new MessageBundle(MessageBundle.LOGOUT);
		bundle.addContent(MessageBundle.ORIGIN_CLIENT, originClient);
		sendMessage(serverName,bundle);
	}
	
	/**
	 * Sends an Instant Message to the destination client.
	 * @param message - Message to send
	 * @param originClient -  Name of the client the login is originating from
	 * @param destinationClient -  Name of the client the login is being sent to
	 * @throws JMSException
	 * @throws NamingException
	 * @author Andrew Thompson
	 */
	public void sendIM(String message, String originClient, String destinationClient) throws JMSException, NamingException{
		//Create a message to send
		MessageBundle bundle = new MessageBundle(MessageBundle.IM);
		bundle.addContent(MessageBundle.MESSAGE, message);
		bundle.addContent(MessageBundle.ORIGIN_CLIENT, originClient);
		sendMessage(destinationClient, bundle);
	}
	
	/**
	 * Sends a message to the destination client, telling them that the active client
	 * has a file ready for them. 
	 * @param originClient
	 * @param destinationClient
	 * @author Andrew Thompson
	 */
	public void sendFileMessage(String originClient, String destinationClient){
		MessageBundle bundle = new MessageBundle(MessageBundle.IM);
		bundle.addContent(MessageBundle.ORIGIN_CLIENT, originClient);
		sendMessage(destinationClient, bundle);
	}

	/**
	 * @param event
	 * @author Andrew Thompson
	 */
	@Override
	public void dispatchEvent(Event event) {
		LoggingManager.logln("Client Network Manager: Dispatching event.");
		event.addResource(Event.CONTROLLER, controller);
		reactor.dispatch(event);
	}

	/**
	 * @param destinationClient
	 * @author Andrew Thompson
	 * @throws NamingException 
	 * @throws JMSException 
	 */
	public void sendClientHandshake(String destinationClient) throws JMSException, NamingException {
		MessageBundle bundle = new MessageBundle(MessageBundle.HANDSHAKE);
		bundle.addContent(MessageBundle.ORIGIN_CLIENT, controller.getUserName());
		bundle.addContent(MessageBundle.FILE_SERVICE, controller.getFileService());
		bundle.addContent(MessageBundle.NETWORK_SERVICE, controller.getNetworkManager().getServerName());
		sendMessage(destinationClient, bundle);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	
	
}
