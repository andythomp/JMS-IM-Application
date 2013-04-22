package edu.carleton.comp4104.assignment3.global;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * The logging manager class is a static class (as static as java gets) that can
 * be accessed from any part of the program. It allows us to use a mechanic similiar to
 * the android log manager.
 * 
 * The main use is that we can configure this using a file external to the program, and then
 * turn off various messages. If we don't want warnings, we can set that. If we don't want errors,
 * we can set that too.
 * 
 * 
 * @author Andrew Thompson
 */
import java.util.Properties;

public class LoggingManager {
	
	/**
	 * CONFIG STRING CONSTANTS
	 */
	private static final String VERBOSE = "VERBOSE";
	private static final String VERBOSE_WARNING = "VERBOSE_WARNING";
	private static final String VERBOSE_ERROR = "VERBOSE_ERROR";
	
	/**
	 * Variables
	 */
	private static boolean verbose;
	private static boolean verboseError;
	private static boolean verboseWarning;
	private static Properties config;
	

	/**
	 * Initializes the Logging Manager using the Logging Manager's configuration file.
	 * @author Andrew Thompson
	 */
	public static boolean initialize(){
		config = ConfigurationManager.getProperty(LoggingManager.class);
		
		if (config == null){
			System.err.println("Error: Unable to retrieve LoggingManager config file.");
			return false;
		}
		
		//Initialize messages
		try{
			
			if (config.getProperty(VERBOSE).equalsIgnoreCase("TRUE"))
				LoggingManager.verbose = true;
			else
				LoggingManager.verbose = false;
			}
		catch (NullPointerException e){
			System.err.println(VERBOSE + " was not found in config file.");
			LoggingManager.verbose = false;
		}
		
		//Initialize warnings
		try{
			if (config.getProperty(VERBOSE_WARNING).equalsIgnoreCase("TRUE"))
				LoggingManager.verboseWarning = true;
			else
				LoggingManager.verboseWarning = false;
		}
		catch (NullPointerException e){
			System.err.println(VERBOSE_WARNING + " was not found in config file.");
			LoggingManager.verbose = false;
		}
		
		
		//Initialize error
		try{
			if (config.getProperty(VERBOSE_ERROR).equalsIgnoreCase("TRUE"))
				LoggingManager.verboseError = true;
			else
				LoggingManager.verboseError = false;
			return true;
		}
		catch (NullPointerException e){
			System.err.println(VERBOSE_ERROR + " was not found in config file.");
			LoggingManager.verbose = false;
		}
		
		return true;
		
	}
	
	/**
	 * Prints a message to the specified output stream and then prints
	 * a new line.
	 * @param message - Message you want to print
	 * @author Andrew Thompson
	 */
	public static void logln(String message){
		if (verbose){
			System.out.println(message);
		}
	}
	
	/**
	 * Prints a message to the specified output stream. Does not print a new line.
	 * @param message - Message you want to print
	 * @author Andrew Thompson
	 */
	public static void log(String message){
		if (verbose){
			System.out.print(message);
		}
	}

	/**
	 * Takes a string which you consider an error and prints it out
	 * to the specified output stream.
	 * *note* errors should be considered things that do not happen through
	 * normal interaction or are fatal. They are things that you need to correct
	 * as a developer.
	 * 
	 * @param message - Error you want to print out
	 * @author Andrew Thompson
	 */
	public static void logerr(String message) {
		if (verboseError){
			System.err.println("Error:" + message);
		}	
	}
	/**
	 * Takes a string which you consider a warning and prints it out
	 * to the specified output stream.
	 * *note* warnings should be considered things that may happen through
	 * normal user interaction that are not fatal but not good.
	 * 
	 * @param message - Warning you want to print out
	 * @author Andrew Thompson
	 */
	public static void logwarn(String message) {
		if (verboseWarning){
			System.err.println("Warning:" + message);
		}	
	}

}
