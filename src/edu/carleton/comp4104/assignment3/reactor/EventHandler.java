package edu.carleton.comp4104.assignment3.reactor;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.io.IOException;

/**
 * The generic event handler interface. All event handlers need to know
 * how to handle an event, but how they handle events is up to them.
 * @author Andrew Thompson
 *
 */
public interface EventHandler {

	public void handleEvent(Event event) throws IOException;
	
}
