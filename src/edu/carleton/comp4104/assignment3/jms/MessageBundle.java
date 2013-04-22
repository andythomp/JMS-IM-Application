package edu.carleton.comp4104.assignment3.jms;

import java.io.Serializable;
import java.util.HashMap;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * MessageBundle is designed to carry a package of information
 * related to sending a message across the wire. Later, it is transformed
 * into a JMS Map Message
 * @author Andrew Thompson
 *
 */
public class MessageBundle implements Serializable {

	
	
	public static final String TYPE = "TYPE";
	/**
	 * TYPE CONSTANTS
	 */
	public static final String FILE = "FILE";
	public static final String PING = "PING";
	public static final String LOGIN = "LOGIN";
	public static final String LOGOUT = "LOGOUT";
	public static final String IM = "IM";
	public static final String ERROR = "ERROR";
	public static final String KEY = "KEY";
	
	/**
	 * PARAMETER CONSTANTS
	 */
	public static final String ORIGIN_CLIENT = "ORIGIN_CLIENT";
	public static final String DESTINATION_CLIENT = "DESTINATION_CLIENT";
	public static final String FILE_SERVICE = "FILE_SERVICE";
	public static final String NETWORK_SERVICE = "NETWORK_SERVICE";
	public static final String MESSAGE = "MESSAGE";
	public static final String BUNDLE = "BUNDLE";
	
	/**
	 * SERIALIZATION ID
	 */
	private static final long serialVersionUID = 4114950879755603608L;
	public static final String HANDSHAKE = "HANDSHAKE";
	
	
	/**
	 * Variables
	 */
	private String header;
	private HashMap<String, Object> body;
	

	/**
	 * Constructs a new message bundle out of a type. Type should be defined based on constants
	 * located in the MessageBody class.
	 * @param type - Type of message to be delivered
	 * @author Andrew Thompson
	 */
	public MessageBundle(String type){
			setBody(new HashMap<String, Object>());
			header = type;
			addContent(KEY, Math.round((Math.random() * 100000000)));
	}
	
	/**
	 * Adds content to the message body
	 * 
	 * @param key - Key of the object to be added
	 * @param value - Object to be added
	 * @author Andrew Thompson
	 */
	public void addContent(String key, Object value){
		body.put(key, value);
	}

	/**
	 * Gets the message type / header
	 * @return - the message type (string)
	 * @author Andrew Thompson
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Sets the message type. Should be set using constants included
	 * in MessageBundle class.
	 * @param header - type to be set
	 * @author Andrew Thompson
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Gets the body of the message
	 * @return - the message body (hash map)
	 * @author Andrew Thompson
	 */
	public HashMap<String, Object> getBody() {
		return body;
	}

	/**
	 * Private method to set the body of the message. Once set,
	 * the only way to add content to the message is with add content.
	 * 
	 * @param body - a new message body
	 * @author Andrew Thompson
	 */
	private void setBody(HashMap<String, Object> body) {
		this.body = body;
	}
	
	/**
	 * Gets a string representation of the message bundle.
	 * @return - string representation of message
	 * @author Andrew Thompson
	 */
	public String toString(){
		return header + ":" + body.toString();
	}

	/**
	 * Gets a resource out of the message body
	 * @param key - key to look up a resource by
	 * @return - an object found in the body, or null if not found
	 * @author Andrew Thompson
	 */
	public Object getResource(String key) {
		return body.get(key);
	}

}
