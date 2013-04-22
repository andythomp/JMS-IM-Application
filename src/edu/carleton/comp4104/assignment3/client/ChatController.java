package edu.carleton.comp4104.assignment3.client;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * Controller for the GUI and the network communications.
 * Ties most of the components together and acts as a communication hub
 * within the program. Anything that needs to update the GUI goes through the controller,
 * and anything that needs to communicate with the outside world also goes through the controller.
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Vector;

import javax.jms.JMSException;
import javax.naming.NamingException;


import edu.carleton.comp4104.assignment3.global.ConfigurationManager;
import edu.carleton.comp4104.assignment3.global.LoggingManager;
import edu.carleton.comp4104.assignment3.jms.ServerList;
import edu.carleton.comp4104.assignment3.rmi.Transfer;
import edu.carleton.comp4104.assignment3.rmi.TransferEngine;

public class ChatController {

	/**
	 * ***PROGRAM STARTS HERE***
	 * Main function
	 * CONFIG_KEY: Used for reading in the initial configuration file.
	 * USER_NAME_KEY: (Optional) will set a preselected user name instead of using a default user name.
	 * @param args - Command line arguments
	 * @author Andrew Thompson
	 */
	 public static void main(String[] args){
	    	
		String userName = null;
		//Read in our command line arguements and parse everything
		try{
		 	for (int i = 0; i < args.length; i++){
		 		if (args[i].equals(CONFIG_KEY)){
		 			if (ConfigurationManager.initialize(args[i+1])){
		 				ServerList.initialize();
						LoggingManager.logln("Initialization was a success.");
					}
					else{
						LoggingManager.logerr("Initialization failed. Terminating.");
						return;
					}
		 		}
		 		else if (args[i].equals(USER_NAME_KEY)){
		 			userName = args[i+1];
		 		}
		 	}
		}
		catch (Exception e){
			System.err.println("Error: Input should be of type:\n" +
					"-c [config_file_name.cfg]" +
					"-u [user name] (Optional)");
			e.printStackTrace();
		}
		if (!ConfigurationManager.isInitialized()){
			System.err.println("Error: Configurations were never initialized.\n" +
					"Input should be of type:\n" +
					"-c [config_file_name.cfg]\n" +
					"-u [user name] (Optional)");
			return;
		}
		//Initialize our controller.
		ChatController controller = new ChatController(userName);
		try {
			controller.initializeNetworkManager();
	//		controller.initializeGUI(userName);
			
		//	controller.runTests();
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
    	
        
	 }
	/**
	 * Variables
	 */
	private String userName;
	private Transfer fileService;
	private Vector<Client> clients;
	private ChatClient clientGUI;
	private ClientNetworkManager networkManager;
    private Client selectedClient;


    /**
     * Used to parse the user input for the file trigger keyword
     */
	private static final String FILE_KEY = "file";
	
    /**
	 * Main Argument Constants
	 * These are required for parsing the initial values passed in through Main()
	 */
	private static final String CONFIG_KEY = "-c";
	private static final String USER_NAME_KEY = "-u";
	
	/**
	 * Initializes the controller with the user name provided
	 * @param userName - User name to be used for logging in and sending messages.
	 * If set to null a random user name is generated.
	 * 	*Important to note this must be unique - used for RMI and JMS
	 * @author Andrew Thompson
	 */
	public ChatController(String userName){
		if (userName!= null)
			this.userName = userName;
		else
			this.userName = "User#" +  Math.round((Math.random() * 10000));
		LoggingManager.logln("Initializing Chat Controller for user: " + this.userName);
		clients = new Vector<Client>();
		
		try {
			this.fileService = new TransferEngine();
           UnicastRemoteObject.exportObject(this.fileService, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * Takes a client and adds it to the list of clients,
     * then it refreshes the userList.
     * @param client - new Client object
     * @author Andrew Thompson
     */
    public synchronized void addClient(Client client){
    	
    	if (client.getUsername().equals(userName)){
    		LoggingManager.logerr("Attempted to add ourself to the client list.");
    		return;
    	}
    	if (lookUpUser(client.getUsername()) != -1){
    		LoggingManager.logerr("Attempted to add user that already exists.");
    		return;
    	}
    	LoggingManager.logln("Adding client:" + client.getUsername() + " to client list.");
    	clients.add(client);
		clientGUI.updateUserList(clients);
		if (selectedClient != null){
			clientGUI.setSelected(lookUpUser(selectedClient.getUsername()));
			clientGUI.updateMessageBox(selectedClient.getMessages());
		}
    }
	
	

	/**
     * Adds a message to the current selected user's message list
     * @param message - Message to be added
     * @author Andrew Thompson
     */
    public synchronized void addMessage(String message){
    	if (selectedClient != null){
    		selectedClient.addMessage(message);
    		clientGUI.updateMessageBox(selectedClient.getMessages());
    		clientGUI.updateUserList(clients);
    	}
    }
	
	 /**
     * Looks up the userName given and adds a message to that user's message list.
     * @param message - Message to be added
     * @param userName - userName of the client
     * @author Andrew Thompson
     */
    public synchronized void addMessage(String message, String userName){
    	int clientIndex = lookUpUser(userName);
    	if (clientIndex == -1){
    		LoggingManager.logwarn("Tried to add a message to a user that does not exist.");
    		return;
    	}
    	Client client = clients.get(clientIndex);
    	if (client==null){
    		LoggingManager.logwarn("Tried to add a message to a user that does not exist.");
    		return;
    	}
     	
    	client.addMessage(message);
    	clientGUI.updateUserList(clients);
    	if (selectedClient != null){
    		clientGUI.updateMessageBox(selectedClient.getMessages());
			clientGUI.setSelected(lookUpUser(selectedClient.getUsername()));
     	}
    	
    }
	/**
     * Gets a vector of clients
     * @return - Client Vector
     * @author Andrew Thompson
     */
	public synchronized Vector<Client> getClients() {
		return clients;
	}
    
    /**
	 * Gets the active client's file service.
	 * @return - Handle to file service
	 * @author Andrew Thompson
	 */
    public Transfer getFileService() {
		return fileService;
	}
    
    /**
     * Gets the handle to the network manager.
     * @return - Handle to network manager
     * @author Andrew Thompson
     */
	public ClientNetworkManager getNetworkManager() {
		return networkManager;
	}
    
    /**
	  * Returns the set User Name
	  * @return - User Name
	  * @author Andrew Thompson
	  */
	public String getUserName() {
		return userName;
	}
    
    /**
	 * Initializes the GUI with the given userName
	 * @param userName - Active client's user name
	 * @author Andrew Thompson
	 */
	public synchronized void initializeGUI(String userName){
		if (ChatClient.isInitialized()){
			return;
		}
		clientGUI = ChatClient.initialize(userName, this);
        clientGUI.setVisible(true);
	}
    
    /**
	 * Initializes the network manager. Necessary to send messages, should be done as part of setup
	 * @throws NamingException
	 * @throws JMSException
	 * @author Andrew Thompson
	 */
	public void initializeNetworkManager() throws NamingException, JMSException{
		networkManager = new ClientNetworkManager(this);
		new Thread(networkManager).start();
		sendLogin();
	}
    
    /**
     * Private function to look up a user. Necessary for removing users, or adding
     * messages to specific users.
     * @param userName - User Name of client to be looked up.
     * @return - The client's index if found, -1 if not
     * @author Andrew Thompson
     */
    private synchronized int lookUpUser(String userName){
    	for (int i = 0; i < clients.size(); i++){
    		if (clients.get(i).getUsername().equals(userName)){
    			return i;
    		}
    	}
    	return -1;
    }
    
    /**
	 * Called when a new client is selected in the GUI.
	 * Updates the selectedClient and re-enables send if it was disabled.
	 * @param selectedIndex - Index of the selected client
	 * @author Andrew Thompson
	 */
	public void newClientSelected(int selectedIndex) {
		if (selectedIndex == -1)
			return;
		if (!clients.get(selectedIndex).equals(selectedClient)){
			clientGUI.enableSend();
			selectedClient = clients.get(selectedIndex);
			clientGUI.updateMessageBox(selectedClient.getMessages());
		}
		
		
	}

	/**
	 * Reads in a file from a given input file name and returns an array of bytes
	 * representing that file.
	 * @param aInputFileName - name of file
	 * @return - byte array representing file
	 * @throws IOException
	 * @author Andrew Thompson
	 */
    private byte[] read(String aInputFileName) throws IOException {
		Path inPath = Paths.get(aInputFileName);
		return Files.readAllBytes(inPath);
	}

	/**
     * Takes a client object, finds the client in the user list, and removes it
     * Then updates the GUI
     * @param client - Client object to be removed.
     * @author Andrew Thompson
     * 
     */
    public synchronized void removeUser(Client client){
    	clients.remove(client);
    	LoggingManager.logln("Client that was just removed is:" + client.getUsername());
    	LoggingManager.logln("Selected Client is:" + selectedClient);
    	clientGUI.updateUserList(clients);
		clientGUI.updateMessageBox(selectedClient.getMessages());
    	if (client.equals(selectedClient)){
    		selectedClient = null;
    		clientGUI.disableSend();
    	}
    	else{
    		clientGUI.setSelected(lookUpUser(selectedClient.getUsername()));	
    	}
    }

	
	 /**
     * Takes a user name, looks up the client in the client list, and removes it
     * Then updates the GUI
     * @param userName - Client to be removed
     * @author Andrew Thompson
     */
    public synchronized void removeUser(String userName){
    	//Do a quick check to make sure that the user does exist.
    	int clientIndex = lookUpUser(userName);
    	if (clientIndex == -1){
    		LoggingManager.logwarn("Tried to remove a user that doesn't exist. User name is:" + userName);
    		return;
    	}
    	//At this point it must exist, so remove it.
    	Client client = clients.get(clientIndex);
    	removeUser(client);
    }


	/**
	 * Tells the network manager to send a client handshake message.
	 * This lets new users know who is currently logged in. When ever a client logs in,
	 * any clients who hear about this should immediately make a hand shake with the new client.
	 * @author Andrew Thompson
	 */
	public void sendClientHandshake(String destinationClient) {
		try {
			networkManager.sendClientHandshake(destinationClient);
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
		}
	}

	
	private HashMap<String, Integer> filesPositions = new HashMap<String, Integer>(); 
	private int divideInto = 1024;
	
	/**
	 * Gets a chunk out of a byte array representing a file
	 * @param key - The file name
	 * @param fileBytes - file bytes representing the file
	 * @return - a chunk of bytes
	 * @author Roger Cheung
	 */
	public byte[] getChunk(String key, byte[] fileBytes) {
		byte[] aChunk;
		int chunkSize;
		int fileLength = fileBytes.length;
		int atIndex = filesPositions.get(key);

		if (fileLength - (atIndex + 1) > divideInto)
			chunkSize = divideInto;
		else // last chunk, chunk is smaller than 1024
			chunkSize = fileLength - atIndex;

		aChunk = new byte[chunkSize];

		for (int i = 0; i < chunkSize; i++) {
			aChunk[i] = fileBytes[atIndex++];
		}
		
		filesPositions.put(key, atIndex);

		return aChunk;
	}
	
	public int getNumberChunks(int fileLength) {
		if (fileLength % divideInto == 0)
			return fileLength / divideInto;
		else
			return fileLength / divideInto + 1;
	}
	
	
	/**
	 * Called when sending a file instead of a text message.
	 * @param text - Should contain file name
	 * @author Andrew Thompson
	 */
	private void sendFile(final String text) {
		Runnable fileTask = new Runnable(){

			@Override
			public void run() {
				final Client c = selectedClient;
				LoggingManager.logln("Sending file:" + text);
				addMessage("Sending file:" + text, selectedClient.getUsername());
				
				//Parse text for file name maybe
				String fileName = text;
				
				//Read in the file
				byte[] fileBytes;
				int fileLength;
				int numChunks;
				
				try {
					
					fileBytes = read(fileName);
					fileLength = fileBytes.length;
					LoggingManager.logln("File is " + fileLength + " in size.");
					numChunks = getNumberChunks(fileLength);
					LoggingManager.logln("Sending file in " + numChunks + " chunks.");
					
					c.getFileService().initializeTransfer(fileName, fileLength);
					filesPositions.put(fileName, 0);
					for (int i = 0; i < numChunks; i++){
						byte[] chunk = getChunk(fileName, fileBytes);
						c.getFileService().transfer(fileName, chunk);
					}
					
					c.getFileService().writeToDisc(fileName);
					String newText = "File " + fileName + " has been sent!";
					addMessage(newText, c.getUsername());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		};
		new Thread(fileTask).start();
	}


	/**
	 * Tells the networkManager to send a log in message
	 * @author Andrew Thompson
	 */
	public void sendLogin() {
		try {
			networkManager.sendLoginMessage(userName, fileService);
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Tells the networkManager to send a log out message
	 * @author Andrew Thompson
	 */
	public void sendLogout() {
		try {
			networkManager.sendLogoutMessage(userName);
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
		}
	}
	/**
	  * This function is called from the GUI when the Send Button is pressed.
	  * It will parse the text and send the appropriate message
	  * Messages can be either File or IM
	  * @param text - Message String
	  * @author Andrew Thompson
	  */
	public void sendMessage(String text) {
		
		//Do a quick check to see if this is a file message
		try{
			java.util.StringTokenizer tokens = new java.util.StringTokenizer(text, ":");
			if (tokens.nextToken().equalsIgnoreCase(FILE_KEY)){
				sendFile(tokens.nextToken());
				return;
			}
		}
		catch (Exception e){
			LoggingManager.logwarn("Not a file message.");
		}
		
		
		//Add a quick message to the gui from You to the selected client
		String newText = "You: " + text;
		addMessage(newText, selectedClient.getUsername());
		
		//Create a message to send
		String message = userName + ": " + text;
		try {
			networkManager.sendIM(message, this.userName , selectedClient.getUsername());	
		} catch (JMSException | NamingException e) {
			//Most likely we have lost the server.
			LoggingManager.logerr("Attempting to send IM Message in ChatController.");
			e.printStackTrace();
		}
	}

	

	
}
