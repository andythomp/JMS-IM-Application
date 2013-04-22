package edu.carleton.comp4104.assignment3.server;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import javax.jms.JMSException;
import javax.naming.NamingException;

import edu.carleton.comp4104.assignment3.global.ConfigurationManager;
import edu.carleton.comp4104.assignment3.jms.ServerList;

public class ChatServer {

	
	/**
	 * Main Argument Constants
	 * These are required for parsing the initial values passed in through Main()
	 */
	private static final String CONFIG_KEY = "-c";
	private static final String SERVER_NAME_KEY = "-s";
	
	private ServerNetworkManager networkManager;
	
	/**
	 * Creates a new Chat Server with the given serverName
	 * @param serverName - Name of the server
	 * @author Andrew Thompson
	 */
	public ChatServer(String serverName){
		try {
			setNetworkManager(new ServerNetworkManager(serverName));
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * ***PROGRAM STARTS HERE***
	 * Main function
	 * CONFIG_KEY: Used for reading in the initial configuration file.
	 * SERVER_NAME_KEY: Used for setting the server you wish run. Will look up the given server in the server list file.
	 * @param args - Command line arguments
	 * @author Andrew Thompson
	 */
	public static void main(String[] args){
	 	
			String serverName = null;
			//Read in our command line arguements and parse everything
			try{
			 	for (int i = 0; i < args.length; i++){
			 		if (args[i].equals(CONFIG_KEY)){
			 			if (ConfigurationManager.initialize(args[i+1])){
			 				ServerList.initialize();
							System.out.println("Initialization was a success.");
						}
						else{
							System.err.println("Initialization failed. Terminating.");
							return;
						}
			 		}
			 		else if (args[i].equals(SERVER_NAME_KEY)){
			 			serverName = args[i+1];
			 		}
			 	}
			}
			//Confirm that input is proper.
			catch (Exception e){
				System.err.println("Error: Input should be of type:\n" +
						"-c [config_file_name.cfg]" +
						"-s [server name] ");
				e.printStackTrace();
			}
			//Make sure the server's configuration manager is initialized.
			if (!ConfigurationManager.isInitialized()){
				System.err.println("Error: Configuration file was not passed; Input should be of type:\n" +
						"-c [config_file_name.cfg]" +
						"-s [server name] ");
				return;
			}
			//Make sure that the server name has been specified.
			if (serverName == null){
				System.err.println("Error: Server name was not specified; Input should be of type:\n" +
						"-c [config_file_name.cfg]" +
						"-s [server name] ");
				return;
			}
			//Create the chat server.
			new ChatServer(serverName);
			
	
	}



	/**
	 * Gets the network manager from the ChatServer
	 * @return - the network manager
	 * @author Andrew Thompson
	 */
	public ServerNetworkManager getNetworkManager() {
		return networkManager;
	}



	/**
	 * Sets the network manager to a new network manager.
	 * Only used during initialization. Should never be called after that.
	 * @param networkManager - the new network manager
	 * @author Andrew Thompson
	 */
	private void setNetworkManager(ServerNetworkManager networkManager) {
		this.networkManager = networkManager;
	}
	
}
