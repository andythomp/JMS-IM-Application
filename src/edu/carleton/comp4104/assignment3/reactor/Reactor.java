package edu.carleton.comp4104.assignment3.reactor;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.io.IOException;
import java.util.Properties;

import edu.carleton.comp4104.assignment3.global.ConfigurationManager;
import edu.carleton.comp4104.assignment3.global.LoggingManager;



public class Reactor {
	
		//STRING CONSTANTS
		public static final String SERVICE_TYPE = "SERVICE_TYPE";
	
		private Services services;
		private Properties config;
		
		/**
		 * Creates a reactor and initializes it according to the configuration manager
		 * 
		 * @author Andrew Thompson
		 */
		public Reactor(){
			config = ConfigurationManager.getProperty(this.getClass());
			if (config == null){
				LoggingManager.logerr("Reactor could not be configured.");
				return;
			}
			loadEventHandlers();
		}
		
		/**
		 * Loads the event handlers based on the services that the reactor is configured for.
		 * @author Andrew Thompson
		 */
		private void loadEventHandlers(){
			LoggingManager.logln("Attempting to load event handlers for: " + config.getProperty(SERVICE_TYPE));
			String clazzName = config.getProperty(SERVICE_TYPE);
			try {
				Class<?> clazz = Class.forName(clazzName);
				services = (Services) clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			LoggingManager.logln("Successfully found class: " + config.getProperty(SERVICE_TYPE));
			services.init();
		}
		
		/**
		 * Dispatches an event to the handler it is designed for/
		 * If that handler does not exist, then it dispatches it to a default handler.
		 * @param event - Event to be handled
		 * @author Andrew Thompson
		 */
		public synchronized void dispatch(Event event){
			EventHandler eHandle;
			if (event == null){
				event = new Event();
				eHandle = services.getDefaultEventHandler();
			}
			else{
				eHandle = services.getEventHandler((String)event.getResource(Event.TYPE));
				if (eHandle == null){
					eHandle = services.getDefaultEventHandler();
				}
			}
			try {
				eHandle.handleEvent(event);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
        
		
        
  
        
}
