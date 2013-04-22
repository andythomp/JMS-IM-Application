package edu.carleton.comp4104.assignment3.jms;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This class is a wrapper for a HashMap that has a TTL on each item
 * added. When a server gets a message, it adds it to this message library.
 * The message will then be deleted after a set amount of time. This ensures we
 * get no duplicate messages.
 */
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import edu.carleton.comp4104.assignment3.global.LoggingManager;


public class MessageLibrary {
	
	private HashMap<Long, MessageBundle> library;
	private Timer timer;
	
	/**
	 * Creates a new message library
	 * 
	 * @author Andrew Thompson
	 */
	public MessageLibrary(){
		library = new HashMap<Long, MessageBundle>();
		timer = new Timer(true);
	}
	
	/**
	 * Checks if a given message is already in the library
	 * @param message - message to check
	 * @return - true if the message exists, false if not
	 * @author Andrew Thompson
	 */
	public boolean messageInLibrary(MessageBundle message){
		if (library.get(message.getResource(MessageBundle.KEY)) != null){
			LoggingManager.logln("MessageLibrary: Message is in library " + message.getResource(MessageBundle.KEY));
			return true;
		}
		else{
			LoggingManager.logln("MessageLibrary: Message is NOT in library " + message.getResource(MessageBundle.KEY));
			return false;
		}
	}
	
	/**
	 * Removes a given message from the message library
	 * @param message - message to remove
	 * @author Andrew Thompson
	 */
	public void removeMessage(MessageBundle message){
		LoggingManager.logln("MessageLibrary: Removing message: " + message.getResource(MessageBundle.KEY));
		library.remove(message.getResource(MessageBundle.KEY));
	}
	
	/**
	 * Adds a message to the library and sets a timer task to remove that message
	 * from the library after a set amount of time.
	 * @param message - Message to add
	 * @author Andrew Thompson
	 */
	public void addMessage(final MessageBundle message){
		LoggingManager.logln("MessageLibrary: Adding message: " + message.getResource(MessageBundle.KEY));
		library.put((Long) message.getResource(MessageBundle.KEY), message);
		TimerTask t = new TimerTask(){
			@Override
			public void run() {
				removeMessage(message);
			}
		};
		timer.schedule(t, 5000);
	}

}
