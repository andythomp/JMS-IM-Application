
package edu.carleton.comp4104.assignment3.jms;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.util.Enumeration;
import java.util.Properties;

import edu.carleton.comp4104.assignment3.global.ConfigurationManager;
import edu.carleton.comp4104.assignment3.global.LoggingManager;

/**
 * This class provides static functions for selecting a server at random,
 * and loading the server list property file.
 * @author Andrew Thompson
 *
 */
public class ServerList {

	private static final String SERVER = "Server";
	private static Properties config;
	
	/**
	 * Initializes the server list by grabbing the list out of the configuration manager.
	 * @author Andrew Thompson
	 */
	public static void initialize() {
		config = ConfigurationManager.getProperty(ServerList.class);
		if (config.size() == 0){
			LoggingManager.logerr("No servers were defined in Server List.");
		}
	}
	
	
	/**
	 * Given a server name gets the number at the end of it.
	 * @param serverName - Server name you want the ID of
	 * @return - The server's ID
	 * @author Andrew Thompson
	 */
	public static int getServerID(String serverName){
		if (serverName == null || serverName.length() == 0){
			return -1;
		}
		return Character.getNumericValue(serverName.charAt(serverName.length()-1));
	}
	
	/**
	 * Gets a random server name out of the list of all available servers.
	 * @return - A random server name
	 * @author Andrew Thompson
	 */
	public static synchronized String getRandomServer(){
		if (config.size() == 0){
			LoggingManager.logerr("Attempted to get random server from empty server list.");
			return null;
		}
		long serverIndex = Math.round((Math.random() * 100000)) % config.size();
		//LoggingManager.logln("Random server selected is number:" + serverIndex);
		Enumeration<Object> keys = config.keys();
		for (int i = 0; i < serverIndex; i++){
			keys.nextElement();
		}
		String serverName = (String) keys.nextElement();
		return serverName;
		
	}
	/**
	 * Gets the previous sequential server, turning back to the end of the server list
	 * if the next server does not exist.
	 * @param serverName - Server name of the server you want the adjacent server of
	 * @return - the previous server name
	 * @author Andrew Thompson
	 */
	public static String getPreviousServer(String serverName){
		int serverID = getServerID(serverName);
		int nextServer = (serverID - 1) % config.size();
		if (nextServer < 0)
			nextServer = config.size() + nextServer;
		return SERVER+nextServer;
	}
	
	/**
	 * Gets the next sequential server, turning back to the start of the server list
	 * if the next server does not exist.
	 * @param serverName - Server name of the server you want the adjacent server of
	 * @return - the next server name
	 * @author Andrew Thompson
	 */
	public static String getNextServer(String serverName){
		int serverID = getServerID(serverName);
		int nextServer = (serverID+1) % config.size();
		return SERVER+nextServer;
	}
	
	/**
	 * Gets the network service string associated with the server name
	 * @param serverName - Name of the server who's network string you require
	 * @return - the server's network string
	 * @author Andrew Thompson
	 */
	public static String getServiceForServer(String serverName){
		return config.getProperty(serverName);
	}

}
