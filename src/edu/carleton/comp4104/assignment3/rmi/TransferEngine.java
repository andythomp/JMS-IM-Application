/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
package edu.carleton.comp4104.assignment3.rmi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import edu.carleton.comp4104.assignment3.global.LoggingManager;


/**
 * @author Roger Cheung
 *
 */
public class TransferEngine implements Transfer, Serializable {

	private static final long serialVersionUID = 4876396361699434027L;
	/**
	 * Group Identities:
	 * Andrew Thompson, SN: 100745521
	 * Roger Cheung, SN: 100741823
	 * Chopel Tsering SN:100649290
	 * 
	 */
	
	private HashMap<String, byte[]> filesInByteForm;
	
	/**
	 * Creates a new transfer engine. This is the file service
	 * by which clients communicate with remote objects.
	 * @author Andrew Thompson
	 */
	public TransferEngine() {
		filesInByteForm  = new HashMap<String, byte[]>();
	}


	/**
	 * 
	 * @param data
	 * @param outPath
	 * @author Andrew Thompson
	 */
	private void write(byte[] data, String outPath) {
		Path path = Paths.get(outPath);
		LoggingManager.logln("Writing file to path:" + path.toString());
		LoggingManager.logln("Writing " + data.length + " bytes to disc.");
		try {
			Files.write(path, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes the file to disc. 
	 * @param fileName - File name string
	 * @author Andrew Thompson
	 */
	public void writeToDisc(String fileName) {
		LoggingManager.logln("Attempting to write file to disc.");
		write(filesInByteForm.get(fileName), "files/" + fileName);
	}

	/**
	 * Transfers a chunk of data across the wire to the client.
	 * @param fileName - the file name string
	 * @author Andrew Thompson
	 * @author Roger Cheung
	 */
	public void transfer(String fileName, byte[] bytes) {
		LoggingManager.logln("Transfering new chunk...");
		byte[] temp = filesInByteForm.get(fileName);
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			outputStream.write(temp);
			outputStream.write( bytes );
			byte[] newFile = outputStream.toByteArray( );
			filesInByteForm.put(fileName, newFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param fileName
	 * @author Andrew Thompson
	 * @author Roger Cheung
	 */
	public void initializeTransfer(String fileName, int fileSize) {
		LoggingManager.logln("Initializing transfer of: " + fileName);
		byte[] temp = new byte[0];
		filesInByteForm.put(fileName,  temp);
		
	}
}
