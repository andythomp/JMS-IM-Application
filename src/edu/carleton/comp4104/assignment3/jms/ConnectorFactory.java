package edu.carleton.comp4104.assignment3.jms;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConnectorFactory {
	
	/**
	 * Returns a ConnectionFactory specific to the provider string provided.
	 * @param provider - Service string of the provider
	 * @return - A new connection factory for the provider
	 * @throws NamingException
	 * @author Tony White
	 */
	public static ConnectionFactory getConnectionFactory(String provider) throws NamingException{
		Properties props = new Properties();
		//This seems to pass in a class name, ActiveMQInitialContextFactory
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL, provider);
		InitialContext jndiContext = null;
		
		/*
		 * Create a JNDI API InitialContext object
		 */
		try {
			jndiContext = new InitialContext(props);
		} catch (NamingException e) {
			System.err.println("Could not create JNDI API context: " + e.toString());
			return null;
		}

		/*
		 * Look up connection factory.
		 */
		return (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
		
	}


}
