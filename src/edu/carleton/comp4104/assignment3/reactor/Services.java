package edu.carleton.comp4104.assignment3.reactor;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import edu.carleton.comp4104.assignment3.global.ConfigurationManager;
import edu.carleton.comp4104.assignment3.global.LoggingManager;


/**
 * Abstract class representing services. Depending on what set of services you 
 * want the reactor to use, you must implement that concrete version of the servcices
 * @author Andrew Thompson
 *
 */
public abstract class Services {
	
	//STRING CONSTANTS
	private static final String DEFAULT_HANDLE = "UNKNOWN";
	
	/**
	 * VARIABLES
	 */
	private HashMap<String, EventHandler> handlers;
	private Properties config;
	
	/**
	 * Returns an event handler for a given key
	 * @param key - Key to look up an event handler by
	 * @return - the event handler
	 * @author Andrew Thompson
	 */
	public EventHandler getEventHandler(String key){
		return handlers.get(key);
	}
	
	/**
	 * Creates a new services object
	 * @author Andrew Thompson
	 */
	public Services(){
		config = ConfigurationManager.getProperty(this.getClass());
		handlers = new HashMap<String, EventHandler>();
	}

	/**
	 * Initializes the services. Using the configuration manager, this determines what 
	 * services it should load and loads them. 
	 * @author Andrew Thompson
	 */
	public void init(){
		LoggingManager.logln("Initializing " + this.getClass().getName());
		
			Enumeration<Object> enumerator = config.keys();
			while (enumerator.hasMoreElements()){
				String key = (String) enumerator.nextElement();
				try{
					handlers.put(key, createEventHandler(key));
					LoggingManager.logln("Added handler: " + key);
				} catch (ClassNotFoundException e){
					LoggingManager.logerr("Class not found: " + config.getProperty(key));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			
			}
			LoggingManager.logln("Handler loading complete.");
		
	}
	
	/**
	 * Creates a new event handler. Does this using the class loader
	 * and reflection.
	 * @param handlerName - name of the new handler
	 * @return - an event handler
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @author Andrew Thompson
	 */
	private EventHandler createEventHandler(String handlerName)
		throws ClassNotFoundException, IllegalAccessException, InstantiationException{
		String clazzName = config.getProperty(handlerName);
		Class<?> clazz = Class.forName(clazzName);
		return (EventHandler)clazz.newInstance();
	}

	/**
	 * Gets the default event handler
	 * @return - an event handler
	 * @author Andrew Thompson
	 */
	public EventHandler getDefaultEventHandler() {
		return getEventHandler(DEFAULT_HANDLE);
	}


}
