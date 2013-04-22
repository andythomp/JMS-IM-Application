package edu.carleton.comp4104.assignment3.client;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * Model class for a client. Contains everything relevant about
 * a client from it's name to it's file service and message history.
 */
import java.util.ArrayList;

import edu.carleton.comp4104.assignment3.rmi.TransferEngine;
/**
 * Model of a Client. Contains necessary things like user name and
 * message history. Also contains FileTransfer service in time.
 * 
 * @author Andrew Thompson
 */
public class Client{


	private static final String NEW = "*NEW*";
	private boolean hasNewMessages;
	private String userName;
	private ArrayList<String> messages;
	private TransferEngine fileService;
	
	/**
	 * Creates a new Client with the given user name.
	 * @param userName - User Name for the client
	 * @author Andrew Thompson
	 */
	

	/**
	 * Creates a new Client with the given user name and file service.
	 * @param userName - User name for the client
	 * @param fileService - File service handle for the client
	 * @author Andrew Thompson
	 */
	public Client(String userName, TransferEngine fileService) {
		hasNewMessages = true;
		this.fileService = fileService;
		setUsername(userName);
		messages = new ArrayList<String>();
	}

	/**
	 * Adds a message to this client's message array.
	 * @param message - Message to be added
	 * @author Andrew Thompson
	 */
	public void addMessage(String message){
		messages.add(message);
		hasNewMessages = true;
	}
	
	/**
	 * Gets a string formatted version of the client's message history
	 * @return - String format of all Messages
	 * @author Andrew Thompson
	 */
	public String getMessages(){
		hasNewMessages = false;
		String temp = "Talking to: " + userName + "\n";
		for (int i = 0; i < messages.size(); i++){
			temp = temp + messages.get(i) + "\n";
		}
		return temp;
	}
	
	/**
	 * Get's the client's RMI File service
	 * @return - reference to the client's file service
	 * @author Andrew Thompson
	 */
	public TransferEngine getFileService() {
		return fileService;
	}
	
	
	/**
	 * Gets the client's user name.
	 * @return - userName
	 * @author Andrew Thompson
	 */
	public String getUsername() {
		return userName;
	}
	
	/**
	 * Sets the client's user name.
	 * @param username - User name to be set
	 * @author Andrew Thompson
	 */
	public void setUsername(String username) {
		this.userName = username;
	}
	
	/**
	 * Returns a string representing the client. Will be specially formatted
	 * if it has a new message or if it is a new user.
	 * @return - userName (modified if it has new messages)
	 * @author Andrew Thompson
	 */
	public String toString(){
		if (hasNewMessages){
			return NEW + " " + userName;
		}
		return userName;
	}

}
