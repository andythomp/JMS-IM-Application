package edu.carleton.comp4104.assignment3.reactor;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.util.HashMap;


/**
 * Events are hashmaps with types, that get passed to event handlers.
 * They just contain a variety of information that will be required for
 * the handler they are destined for.
 * @author Andrew Thompson
 *
 */
public class Event{
	
	public static final String TYPE = "type";

	public static final String CONTROLLER = "controller";
	public static final String NETWORK_MANAGER = "networkmanager";
	public static final String BUNDLE = "bundle";
	
	private HashMap<String, Object> resources;
	
	/**
	 * Creates a basic event
	 * @author Andrew Thompson
	 */
	public Event(){
		resources = new HashMap<String, Object>();
	}
	
	
	/**
	 * Creates an event with a specific type
	 * @param type - Type of event 
	 * @author Andrew Thompson
	 */
	public Event(String type) {
		resources = new HashMap<String, Object>();
		addResource(Event.TYPE, type);
	}


	/**
	 * Adds a resource to the given event.
	 * @param key - key to store the event too
	 * @param resource - object to be stored
	 * @author Andrew Thompson
	 */
	public void addResource(String key, Object resource){
		resources.put(key, resource);
	}
	
	/**
	 * Gets an object for a given key
	 * @param key - object's key
	 * @return - the object
	 * @author Andrew Thompson
	 */
	public Object getResource(String key){
		return resources.get(key);
	}
	
	public String toString(){
		return "Event:" + resources.toString();
	}

	
}
