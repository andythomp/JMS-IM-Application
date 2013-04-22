package edu.carleton.comp4104.assignment3.server;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.activemq.broker.BrokerService;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.Broker;
import edu.carleton.comp4104.assignment3.jms.NetworkManager;
import edu.carleton.comp4104.assignment3.jms.ServerList;
import edu.carleton.comp4104.assignment3.reactor.Event;

/**
 * Server's concrete implementation of the network manager class
 * @author Andrew Thompson
 *
 */
public class ServerNetworkManager extends NetworkManager{

	private BrokerService broker;
	private String serverName;
	
	
	/**
	 * Creates a new Server Network Manager.
	 * Part of this involves creating a Broker and starting it up.
	 * The server then acts as a thin layer over top of the broker.
	 * @param serverName - Name of the server to initialize
	 * @throws NamingException
	 * @throws JMSException
	 * @author Andrew Thompson
	 */
	public ServerNetworkManager(String serverName) throws NamingException, JMSException{
		super();
		this.serverName = serverName;
		boolean nextServerRunning = false;
		boolean previousServerRunning = false;
		try {
			LoggingManager.logln("Please wait while broker initializes...");
			broker = Broker.startBroker(serverName, ServerList.getServiceForServer(serverName), null);
			while (!broker.isStarted()){
				
			}
			LoggingManager.logln("Initialized!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addPublisher(CLIENTS, this.serverName);
		addSubscriber(this.serverName, this.serverName);
		
		//Now that our server is running, wait until the next and previous server is also running.
		while (!nextServerRunning){
			try {
				addPublisher(ServerList.getNextServer(serverName), ServerList.getNextServer(serverName));
				nextServerRunning = true;
			}
			catch(Exception e){
				LoggingManager.logln("Next Server not running yet.");
			}
		}
		
		while (!previousServerRunning){
			try {
				addPublisher(ServerList.getPreviousServer(serverName), ServerList.getPreviousServer(serverName));
				previousServerRunning = true;
			}
			catch(Exception e){
				LoggingManager.logln("Previous Server not running yet.");
			}
		}
		

		
	}

	/**
	 * Takes an event and dispatches it to the correct event handler.
	 * @param event - event to be dispatched
	 * @author Andrew Thompson
	 */
	@Override
	public void dispatchEvent(Event event) {
		event.addResource(Event.NETWORK_MANAGER, this);
		reactor.dispatch(event);
	}

	
}
