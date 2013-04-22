package edu.carleton.comp4104.assignment3.global;

/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 * This class is used to load in a directory of configuration files.
 * Typical configuration file looks like this:
 * *****************************************
 * CONFIG_DIRECTORY=serverconfig/
 * CONFIG_NAME=configurationmanager.cfg
 * VERBOSE=true
 * *****************************************
 * 
 * -The config directory is the directory for all configuration files for the specified
 *  instance of configuration manager.
 * -The config name is the configuration file for the configuration manager.
 * -Verbose is for setting the logging manager, and allows for a customizable level of
 *  messages, based on whether you wish to debug or only see errors. This allows all the
 *  usual System.out.println commands to be disabled without recompiling.
 *  
 *  To establish a class's configuration file, list it's class name and directory in the configuration
 *  manager's configuration file, along with that class's configuration file name. Then, create that
 *  class's configuration file in the specified location, or if no location is specified then in the
 *  same directory. That file will then be loaded.
 *  
 *  This can now be accessed through the configuration manager.

 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

public class ConfigurationManager {

	//KEY CONSTANTS
	public static final String CONFIG_DIRECTORY = "CONFIG_DIRECTORY";
	public static final String CONFIG_NAME = "CONFIG_NAME";
	public static final String VERBOSE = "VERBOSE";
	public static final String SERVER_INIT_FILE_NAME = "serverinit.cfg";
	public static final String CLIENT_INIT_FILE_NAME = "clientinit.cfg";
	
	//Configuration Variables
	private static boolean initialized = false;
	private static HashMap<String, Properties> configurations;
	private static HashMap<Properties, String> fileNames;
	private static String directory;
	
	
	
	/**
	 * This function initializes all the property files stored on disc.
	 * This also initializes the logging manager.
	 * Eventually, these should be separated and more independent. For now, it will be allowed.
	 * @param fileName - Name of the initial configuration file.
	 * @author Andrew Thompson
	 */
	public static boolean initialize(String fileName){
		boolean success = true;
		configurations = new HashMap<String,Properties>();
		fileNames = new HashMap<Properties,String>();
		//Load in our initial configuration file
		FileInputStream fileInput;
		Properties initConfig = new Properties();
		try {
			//Load our initial configurations
			fileInput = new FileInputStream(fileName);
			initConfig.load(fileInput);
			directory = initConfig.getProperty(CONFIG_DIRECTORY);
			
			
			//Get the list of config files name
			String configFile = directory + initConfig.getProperty(CONFIG_NAME);
			
			//Load the list of config files now
			fileInput = new FileInputStream(configFile);
			initConfig.clear();
			initConfig.load(fileInput);
			//Close our file input stream
			fileInput.close();
		}  catch (IOException e) {
			//If main config file is not found, fatal error. return
			e.printStackTrace();
			return false;
		}
		
		//Go through all the config files and load them into properties
		for (Entry<Object, Object> set: initConfig.entrySet()) {
			
			//Get the key and filename
			String tempFileName = (String) set.getValue();
			tempFileName = directory + tempFileName;
			String key = (String) set.getKey();
			
			try {
				//Initialize our file input stream
				fileInput = new FileInputStream(tempFileName);
				
				//Load the new property
				Properties tempProp = new Properties();
				tempProp.load(fileInput);
				
				//Store the old file name (necessary for saving later)
				fileNames.put(tempProp, tempFileName);
				
				//Add it to our configuration map
				configurations.put(key, tempProp);
				fileInput.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				success = false;
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
			}
		}
		LoggingManager.initialize();
		
		//Once we are initialized, set it
		setInitialized();
		return success;
	}
	
	/**
	 * Looks up a class's configuration based on it's class name and hand's the class's
	 * configuration back.
	 * @param key - Instance of a class that you want configurations for.
	 * @return Configuration file of the class you want configurations for (as a property object).
	 * @author Andrew Thompson
	 */
	public static synchronized Properties getProperty(Class<?> key) throws NullPointerException{
		try{
			return configurations.get(key.getName());
		}
		catch (Exception e){
			throw e;
		}
	}
	
	
	/**
	 * This method updates a classes configuration, by passing in it self as a key and
	 * passing in a property file. Note that these configuartions are global configurations,
	 * and so one must be careful not to overwrite a class's configuration file using multiple
	 * instances of a class.
	 * @param key - Instance of a class that you want to update configurations for.
	 * @param property - Configuration object that you want to update.
	 */
	public static synchronized void updateProperty(Class<?> key, Properties property){
		if (property != null && configurations.get(key.getClass().getName()) != null){
			configurations.put(key.getClass().getName(), property);
		}
		else{
			System.err.println("Error updating configurations to key: " + key
					+ " with properties " + property);
		}
	}
	
	/**
	 * This will tell the configuration manager to write all property files to disk.
	 * An improvement to consider is flagging updated property files and only writing
	 * the updated files.
	 * 
	 */
	public static synchronized void saveProperties() throws IOException{
		LoggingManager.logln("Saving all property files...");
		java.util.Collection<Properties> properties = configurations.values();
		Iterator<Properties> i = properties.iterator();
		while (i.hasNext()){
			Properties prop = i.next();
			System.out.println("Saving " + fileNames.get(prop) + " ... ");
			FileOutputStream output = new FileOutputStream(fileNames.get(prop));
			prop.store(output, null);
			output.close();
			System.out.println("Saved!");
		}
		
	}
		
	/**
	 * We use an initialization flag to confirm that the configuration manager is configured.
	 * This may be necessary in concurrent programming, and can also be used to check that
	 * initialization was at least run.
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * Internal function to set initialized to true. Once initialized, can not uninitialize.
	 */
	private static void setInitialized() {
		initialized = true;
	}
	
	
	
}
