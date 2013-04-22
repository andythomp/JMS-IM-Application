package edu.carleton.comp4104.assignment3.jms;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * Subscribers subscribe to a topic and listen for messages about that topic.
 * Each subscriber listens for a single topic on a single service.
 */
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.global.Marshaller;
import edu.carleton.comp4104.assignment3.reactor.Event;

public class Subscriber implements MessageListener {

	
	/**
	 * JMS Variables
	 */
	private Connection connection;
	private ConnectionFactory factory;
	private Session session;
	private Topic topic;
	private MessageConsumer consumer;
	private MessageLibrary library;

	private NetworkManager networkManager;
	
	
	/**
	 * Creates a subscriber for the specified topic and server.
	 * @param topicString - Topic to read about
	 * @param serverName - Server name of the server you want to read at
	 * @param networkManager - Handle back to the network manager
	 * @throws NamingException
	 * @throws JMSException
	 * @author Andrew Thompson
	 */
	public Subscriber(String topicString,String serverName, NetworkManager networkManager) throws NamingException, JMSException {
		connection = null;
		library = new MessageLibrary();
		this.networkManager = networkManager;
		startListening(topicString, serverName);
	}



	/**
	 * Starts listening about the topic
	 * @param topicString - Topic to read about
	 * @param serverName - Server name of the server you want to read at
	 * @throws NamingException
	 * @throws JMSException
	 * @author Andrew Thompson
	 */
	public void startListening(final String topicString, final String serverName) throws NamingException, JMSException {
	
			String service = ServerList.getServiceForServer(serverName);
			//Get our connection factory
			factory = ConnectorFactory.getConnectionFactory(service);
			
			//Create our connection
			connection = factory.createConnection();
			connection.setExceptionListener(new ExceptionListener(){
				@Override
				public void onException(JMSException arg0) {
					LoggingManager.logerr("Massive error, we lost connection.");
					try {
						startListening(topicString, ServerList.getNextServer(serverName));
					} catch (NamingException | JMSException e) {
						e.printStackTrace();
					}
				}
			});
			//Create a session off the connection
			session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			
			//Get the topic
			topic = session.createTopic(topicString);
			
			//Create a consumer off the session
			consumer = session.createConsumer((Destination) topic);
			//Set our event listener
			consumer.setMessageListener(this);
			//Connect and wait for messages
			connection.start();
			
		
	}

	/**
	 * When we get a message for us, we will pass it back to our network manager's reactor
	 * to handle. Before that, we will keep a record of the message in our message library.
	 * @param message - Message received from the wire.
	 * @author Andrew
	 */
	public void onMessage(Message message) {
		LoggingManager.logln("Subscriber" + this.hashCode() + ": Received Message!");
		if (message instanceof MapMessage) {
			MapMessage mapMsg = (MapMessage) message;
			Event event = new Event();
			try {
				MessageBundle bundle = (MessageBundle) Marshaller.deserializeObject(mapMsg.getBytes(MessageBundle.BUNDLE));
				if (library.messageInLibrary(bundle)){
					LoggingManager.logln("Subscriber: Found message in library");
					return;
				}
				library.addMessage(bundle);
				event.addResource(Event.TYPE, mapMsg.getJMSType());
				event.addResource(Event.BUNDLE, bundle);
			} catch (JMSException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
			networkManager.dispatchEvent(event);
		}
	}

	
}
