package edu.carleton.comp4104.assignment3.client;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * This class represents the GUI that the user interacts with.
 * It's size, and it's title, are both read in from the gui.cfg file.
 * 
 */
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.carleton.comp4104.assignment3.global.ConfigurationManager;
import edu.carleton.comp4104.assignment3.global.LoggingManager;


public class ChatClient extends JFrame {
	
	 /* 
	  * ******************************************************************************************
      * LISTENERS START
      * ******************************************************************************************
      */
    /**
     * This listener is called when the user attempts to close the window.
     * @author Andrew Thompson
     *
     */
	public class OnCloseWindowListener implements WindowListener {
		/**
		 * Tells the controller to send a logout message to the server.
		 * @param arg0 - Event in which a window is closing
		 * @author Andrew Thompson
		 */
		@Override
		public void windowClosing(WindowEvent arg0) {
			LoggingManager.logln("Closing window.");
			controller.sendLogout();
		}
		/*
		 * THE FOLLOWING METHODS IN THIS LISTENER ARE NOT OVERRIDDEN
		 */
		@Override
		public void windowActivated(WindowEvent arg0) {}
		@Override
		public void windowDeactivated(WindowEvent arg0) {}
		@Override
		public void windowDeiconified(WindowEvent arg0) {}
		@Override
		public void windowIconified(WindowEvent arg0) {}
		@Override
		public void windowOpened(WindowEvent arg0) {}
		@Override
		public void windowClosed(WindowEvent arg0) {}

	}
	
    /**
     * This listener gets called when ever the user types a key in the chat box
     * @author Andrew Thompson
     */
    private class EnterKeyListener implements KeyListener{
    	/**
		 * Checks the key event to see if it is the Enter key, and also checks to make sure sending is enabled.
		 * Then if it is, performs the same duties as pressing the send button. That is to say, sends the message.
		 * 
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && sendEnabled()){
				controller.sendMessage(chatBox.getText());
	    		chatBox.setText("");
			}
		}
		/*
		 * THE FOLLOWING METHODS IN THIS LISTENER ARE NOT OVERRIDDEN
		 */
		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
    }
	
	/**
     * This listener is called when the Send Button is pressed.
     * @author Andrew Thompson
     *
     */
    private class SendMessageListener implements ActionListener{  
    	public void actionPerformed(ActionEvent event){  
    		controller.sendMessage(chatBox.getText());
    		chatBox.setText("");
       }
       
    }
	/**
     * This listener gets called when ever the user clicks on a client in the client list.
     * @author Andrew Thompson
     */
    private class UserSelectedListener implements ListSelectionListener{

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			try{
				if (clientList.getSelectedIndex() == -1)
					return;
				else{
					controller.newClientSelected(clientList.getSelectedIndex());
				}
			}
			catch (NullPointerException e){
				e.printStackTrace();
			}
		}
	
    }
    /* ******************************************************************************************
     * LISTENERS END
     * ******************************************************************************************
     */
    
    
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -5495102580457556297L;
	
	/**
	 * CONFIG STRING CONSTANTS
	 */
	private static final String TITLE = "TITLE";
	private static final String CLIENT_WIDTH = "WIDTH";
	private static final String CLIENT_HEIGHT = "HEIGHT";
		
    /**
	 * BACK UP CONSTANTS
	 */
	private static final int WIDTH = 500;
    private static final int HEIGHT = 400;
    private static ChatClient singleton = null;
    /**
	 * Java GUI Variables
	 */
	private JLabel userLabel;
    private JList<Client> clientList;
    private JTextArea messageBox;
    private JTextField chatBox;
    private JButton sendButton;
    
    /**
     * Java Model Variables
     */
    private String userName;
    private Properties config;
    private ChatController controller;
    
    /**
     * Builds a GUI Interface
     * @param userName - Name of the user who is logged in
     * @param controller - Handle back to the Chat Controller
     * @author Andrew Thompson
     */
    public ChatClient(String userName, ChatController controller) {
		super();
		this.controller = controller;
		
		//Get the configuration for the GUI
		config = ConfigurationManager.getProperty(this.getClass());
		this.setTitle(config.getProperty(TITLE));
		this.userName = userName;
		
		//Pane is made with a Grid Bag Layout
		JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,0,0,0);	
        
        //This is the user label we see at the top
        userLabel = new JLabel("User: " + this.userName);
        c.weightx = 1;
        c.weighty = .02;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridwidth = 2;
        pane.add(userLabel, c);
        
        //This is the user list
       
        clientList = new JList<Client>(controller.getClients());
        clientList.addListSelectionListener(new UserSelectedListener());
        JScrollPane scrollPane = new JScrollPane(clientList);
        c.weightx = 1;
        c.weighty = .4;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0,0,0,0);	
        pane.add(scrollPane, c);
		
  
        
		//This is our message field
        messageBox = new JTextArea(10, 50);
        messageBox.setEditable(false);
        scrollPane = new JScrollPane(messageBox);
        c.weightx = 1;
        c.weighty = .43;
		c.gridx = 0;
		c.gridy = 2;
        pane.add(scrollPane, c);
        

		
     
        //This is our chat box to send messages
        chatBox = new JTextField();
        chatBox.addKeyListener(new EnterKeyListener());
        c.weightx = .75;
        c.weighty = .02;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.PAGE_END;
		pane.add(chatBox, c);
        
        //This is our send button
        sendButton = new JButton("Send");
        sendButton.addActionListener(new SendMessageListener());
        sendButton.setEnabled(false);
        
        sendButton.registerKeyboardAction(sendButton.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        sendButton.registerKeyboardAction(sendButton.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);

	
        c.weightx = .25;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 3;
		pane.add(sendButton,c );
			
 
		int width, height;
		try{
			width = Integer.parseInt(config.getProperty(CLIENT_WIDTH));
			height = Integer.parseInt(config.getProperty(CLIENT_HEIGHT));
		}
		catch (Exception e){
			LoggingManager.logerr("Error: "+CLIENT_WIDTH+" is not defined in the config file.");
			LoggingManager.logerr("Error: "+CLIENT_HEIGHT+" is not defined in the config file.");
			width = WIDTH;
			height = HEIGHT;
		}
		
        //Finish up
        this.setContentPane(pane);
        this.getContentPane().setPreferredSize(new Dimension(width, height));
        													
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new OnCloseWindowListener());
        this.pack();         
    }
    
    /**
     * Checks to if the GUI is initialized
     * @return - boolean representing the status of the GUI
     * @author Andrew Thompson
     */
    public static boolean isInitialized(){
    	if (singleton == null){
    		return false;
    	}
    	else{
    		return true;
    	}
    }
    
    /**
     * Initializes the GUI with the given user name and a handle back to the controller.
     * @param userName - Client's user name
     * @param controller - Handle back to controller
     * @return - a ChatClient GUI
     * @author Andrew Thompson
     */
    public static ChatClient initialize(String userName, ChatController controller){
    	if (ChatClient.singleton != null){
			return singleton;
		}
    	else{
    		singleton = new ChatClient(userName, controller);
    		return singleton;
    	}
    }
    
    /**
     * Disables sending from the GUI
     * @author Andrew Thompson
     */
    public void disableSend(){
    	sendButton.setEnabled(false);
    }
    
    /**
     * Enables sending from the GUI
     * @author Andrew Thompson
     */
    public void enableSend(){
    	sendButton.setEnabled(true);
    }
    
    /**
     * Checks if sending is currently allowed
     * @return - sendButton's enabled status
     * @author Andrew Thompson
     */
    public boolean sendEnabled(){
    	return sendButton.isEnabled();
    }
    
    /**
     * Sets the selected user in the user list
     * @param index - Index of the selected user.
     * @author Andrew Thompson
     */
    public void setSelected(int index){
    	try{
    		clientList.setSelectedIndex(index);
    		sendEnabled();
    	}
    	catch (Exception e){
    		LoggingManager.logwarn("Attempting to highlight the selected client.");
    	}
    }
    
    /**
     * Takes a text string and updates the message box with the string.
     * For purposes of this implementation, pass in a client's getMessages().
     * @param text - New message box text
     * @author Andrew Thompson
     */
    public synchronized void updateMessageBox(final String text){
    	LoggingManager.logln("GUI: Updating message box.");
    	if (text != null){
    		Runnable worker = new Runnable() {  
    			public void run() {
    				messageBox.setText(text);
    			} 
    		};
        	SwingUtilities.invokeLater(worker);
    	}
    }
    
    /**Takes a list of clients and updates the user list.
     * @param list - New client list data. Must be a list of clients for
     * this implementation
     * @author Andrew Thompson
     */
    public synchronized void updateUserList(final Vector<Client> list){
    	LoggingManager.logln("GUI: Updating user list.");
    	Runnable worker = new Runnable() {  
			public void run() {
				clientList.setListData(list);
			} 
		};
    	SwingUtilities.invokeLater(worker);
    }
    
   
    
    
}
