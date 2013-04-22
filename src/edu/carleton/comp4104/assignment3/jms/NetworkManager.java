package edu.carleton.comp4104.assignment3.jms;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This abstract class represents what every network manager needs,
 * a set of publishers and subscribers, as well as some functions to
 * send and broad cast messages
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.jms.JMSException;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.ConfigurationManager;
import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.Reactor;

/**
 * Abstract class to streamline most reusable code into a single
 * space. Must be extended in order to provide a concrete implementation of standard 
 * communication practices.
 * 
 * @author Andrew Thompson
 *
 */
public abstract class NetworkManager implements Runnable{

	/**
	 * CONFIGURATION MANAGER CONSTANTS
	 */
	public final String SERVICE = "SERVICE";
	public final String TOPIC = "TOPIC";
	
	/**
	 * JMS CONSTANTS
	 */
	public final String CLIENTS = "Clients";
	
	
	/**
	 * VARIABLES
	 */
	protected boolean running;
	protected HashMap<String, Publisher> publishers;
	protected HashMap<String, Subscriber> subscribers;
	protected Reactor reactor;
	protected Properties config;
	
	/**
	 * Creates a new NetworkManager. Must be overridden by all other network managers.
	 * @throws NamingException
	 * @throws JMSException
	 * @author Andrew Thompson
	 */
	public NetworkManager() throws NamingException, JMSException{
		config = ConfigurationManager.getProperty(this.getClass());
		running = true;
		reactor = new Reactor();
		publishers = new HashMap<String, Publisher>();
		subscribers = new HashMap<String, Subscriber>();
	}
	


	/**
	 * Creates a new publisher and adds it to the subscriber map
	 * @param publisherName - name, key, and topic of the publisher
	 * @param serverName - Name of the server to publish at
	 * @author Andrew Thompson
	 * @throws JMSException 
	 * @throws NamingException 
	 */
	public synchronized void addPublisher(String publisherName, String serverName) throws NamingException, JMSException{
		Publisher publisher = new Publisher(serverName, publisherName);
		publishers.put(publisherName, publisher);
	}
	
	/**
	 * Creates a new subscriber and adds it to the subscriber map
	 * @param subscriberName - name, key, and topic of the subscriber
	 * @param service - service string of the destination 
	 * @author Andrew Thompson
	 */
	public synchronized void addSubscriber(String subscriberName, String serverName) throws NamingException, JMSException{
		Subscriber subscriber = new Subscriber(subscriberName, serverName, this);
		subscribers.put(subscriberName, subscriber);
	}
	
	/**
	 * Looks up a publisher in the publisher map and tells it to send a message bundle.
	 * @param publisherName - name of the publisher and topic you want to write about
	 * @param bundle - MessageBundle you have created prior to calling method
	 * @author Andrew Thompson
	 * @throws NamingException 
	 * @throws JMSException 
	 */
	public synchronized void sendMessage(String publisherName, MessageBundle bundle){
		try {
			publishers.get(publisherName).sendMessage(bundle);
		} catch (Exception e) {
			LoggingManager.logerr("SendMessage: Attempting to send message to another server.");
		}
	}
	
	
	/**
	 * Given a message, this broadcasts the message to all known connections by writing it
	 * to every publisher it is aware of.
	 * @param bundle - MessageBundle to be sent
	 * @throws JMSException
	 * @throws NamingException
	 * @author Andrew Thompson
	 */
	public void broadCastMessage(MessageBundle bundle) throws JMSException, NamingException{
		Iterator<Publisher> publisherIterator = publishers.values().iterator();
		while (publisherIterator.hasNext()){
			Publisher publisher = publisherIterator.next();
			try {
				LoggingManager.logln("Sending:" + bundle.getHeader() + " message to:" + publisher.getTopicString());
				publisher.sendMessage(bundle);
			} catch (Exception e) {
				LoggingManager.logerr("BroadCastMessage: Attempting to send message to another server.");
			}
		}
	}

	
	/**
	 * Dispatches an event to the reactor, which ultimately calls an EventHandler of some kind
	 * that has been registered to the reactor.
	 * @param event - Event to be handled
	 * @author Andrew Thompson
	 */
	public abstract void dispatchEvent(Event event);
	
	@Override
	public void run(){
		while(running){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	
	
}
