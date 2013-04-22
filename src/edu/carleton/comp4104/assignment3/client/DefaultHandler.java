package edu.carleton.comp4104.assignment3.client;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This handler is called when ever an event of unknown type is received.
 * It should just log out the event to the logging manager.
 */
import java.io.IOException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.EventHandler;

public class DefaultHandler implements EventHandler {


	/**
	 * This will just say that an unknown event was received, and output it to
	 * the log.
	 * @param event - Event to be handled
	 * @throws IOException
	 * @author Andrew Thompson
	 */
	@Override
	public void handleEvent(Event event) throws IOException {
		LoggingManager.logln("Received an unknown event:");
		LoggingManager.logln(event.toString());
		
	}

}
