package edu.carleton.comp4104.assignment3.global;

/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * Currently this class does not initialize or work. Eventually, this manager will
 * be used to handle errors in a more sophisticated way, and will ultimately let us 
 * keep a property file of Error codes and error messages. These will be loaded in on initialization,
 * and when error's occur we will get them from the file.
 * 
 * This will be immensely useful for a distributed program, as we can send error's to clients or servers that are
 * not immediately determined at compile time. This allows us to update parts of the server or client, which may
 * in turn generate new errors, and update those error's as well, all without redistributing the project itself.
 * 
 * @author Andrew Thompson
 */

import java.util.Properties;

public class ErrorManager {

	private static Properties config;
	
	/**
	 * Load in the error configuration file.
	 * 
	 * @author Andrew Thompson
	 */
	public static void initialize(){
		config = ConfigurationManager.getProperty(ErrorManager.class);
		if (config == null){
			LoggingManager.logln("Error: passed in a null config file.");
		}
	}
	
	
}
