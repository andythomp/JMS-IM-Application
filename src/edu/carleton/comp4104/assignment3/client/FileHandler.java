package edu.carleton.comp4104.assignment3.client;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This is called when it receives a request for a file transfer. It gets passed
 * an event that holds a client's name. The handler then uses that name to look up
 * the client, and grabs that client's file transfer service, and uses it to download
 * the file.
 */
import java.io.IOException;

import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.reactor.Event;
import edu.carleton.comp4104.assignment3.reactor.EventHandler;

public class FileHandler implements EventHandler{
	@Override
	public void handleEvent(Event event) throws IOException {
		LoggingManager.logln("File handler called.");
		
	}

}
