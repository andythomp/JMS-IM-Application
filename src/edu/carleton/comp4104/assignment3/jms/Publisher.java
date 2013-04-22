package edu.carleton.comp4104.assignment3.jms;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * Publishers lock in on a specific Topic and Service and then grant
 * the Network Manager the ability to send messages on that Topic and Service.
 * A single publisher is required for a single service and topic.
 */
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.global.Marshaller;

public class Publisher{
	/**
	 * JMS Variables
	 */
	private Connection connection;
	private ConnectionFactory factory;
	private Session session;
	private Topic topic;
	private MessageProducer producer;
	
	
	private String topicString;
	
	public String getTopicString() {
		return topicString;
	}

	/**
	 * Given an address and a topic, creates a publisher which can be used to send
	 * map messages.
	 * @param serverName - Name of the server to start the publisher on.
	 * @param topicString - The Topic to write about.
	 * @throws NamingException 
	 * @throws JMSException 
	 * @author Andrew Thompson
	 */
	public Publisher(String serverName, String topicString) throws NamingException, JMSException {

		this.topicString = topicString;
		//Create a connection off of the connector factory
		startPublishing(serverName, topicString);
		

		
			
		
	}
	
	/**
	 * Sends a message to the registered location
	 * @param bundle - bundle of information to send
	 * @throws JMSException
	 * @throws NamingException
	 * @throws IOException
	 * @author Andrew Thompson
	 */
	protected void sendMessage(MessageBundle bundle) throws JMSException, NamingException, IOException{
		MapMessage jmsMessage = session.createMapMessage();
		jmsMessage.setJMSType(bundle.getHeader());
		jmsMessage.setBytes(MessageBundle.BUNDLE, Marshaller.serializeObject(bundle));
		
		producer.send(jmsMessage);
	}
	
	/**
	 * Begin the publisher's life of publishing about a specific topic
	 * @param serverName - Server that will be listening to this publisher
	 * @param topicString - String representing the topic to write about
	 * @throws NamingException
	 * @throws JMSException
	 * @author Andrew Thompson
	 */
	protected void startPublishing(final String serverName, final String topicString) throws NamingException, JMSException{
		String service = ServerList.getServiceForServer(serverName);
		
		factory = ConnectorFactory.getConnectionFactory(service);
		connection = factory.createConnection();
		connection.setExceptionListener(new ExceptionListener(){
			@Override
			public void onException(JMSException arg0) {
				LoggingManager.logerr("Massive error, we lost connection.");
				try {
					startPublishing(ServerList.getNextServer(serverName), topicString);
				} catch (NamingException | JMSException e) {
					e.printStackTrace();
				}
			}
		});
		
		//Create a session off the connection
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // false=NotTransacted
		
		//Create a topic
		topic = session.createTopic(topicString);

		//Create a producer off the session
		producer = session.createProducer((Destination) topic);
		producer.setTimeToLive(10000);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		connection.start();
	}

}
