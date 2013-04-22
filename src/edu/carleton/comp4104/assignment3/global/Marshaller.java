package edu.carleton.comp4104.assignment3.global;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * Static class designed to serialize and deserialize objects
 * that implement the Serializable interface.
 * @author Anthony White
 *
 */
public class Marshaller {

	/**
	 * Takes a serializable object and turns it into an array of bytes.
	 * 
	 * @param o - object to be converted to bytes
	 * @return - byte array representing an object
	 * @throws IOException
	 * @author Anthony White
	 */
	public static byte[] serializeObject(Object o) throws IOException {
		if (o == null) {
			return null;
		}
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		objectStream.writeObject(o);
		objectStream.close();

		return byteStream.toByteArray();
	}

	
	/**
	 * Attempts to convert an array of bytes into a serializable object
	 * 
	 * @param data - byte array to be converted into an object
	 * @return - an object implementing the serializable interface
	 * @throws IOException
	 * @author Andrew Thompson
	 */
	public static Object deserializeObject(byte[] data) throws IOException {
		if (data == null || data.length < 1) {
			return null;
		}
		Object object = null;
		try {
			ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(data));
			object = objectStream.readObject();
			objectStream.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}
}
