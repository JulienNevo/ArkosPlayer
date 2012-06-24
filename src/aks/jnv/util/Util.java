package aks.jnv.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jp.gr.java_conf.dangan.util.lha.LhaChecksum;
import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaInputStream;

/**
 * Some utility methods.
 * 
 * @author Julien Névo
 *
 */
public class Util {

	/**
     * Reads the bytes of an input stream.
     * @param inputStream an input stream from which to read the application.
     * @return the bytes read from the given input stream.
     * @throws IOException if a problem occurs during reading.
     */
    public static byte[] readInputStream(final InputStream inputStream) throws IOException {
    	byte[] result = null;
    	
    	if (inputStream == null) {
            throw new IOException("File can't be opened");
        } else {
        	ByteArrayOutputStream buffer = null;
        	
        	try {
	        	buffer = new ByteArrayOutputStream();
	        	
	        	byte[] dataRead = new byte[16384];
	        	int nbBytesRead;
	        	
	        	while ((nbBytesRead = inputStream.read(dataRead)) != -1) {
	        		buffer.write(dataRead, 0, nbBytesRead);
	        	}
	        	
	        	buffer.flush();
	        	result = buffer.toByteArray();
        	} catch (IOException e) {
        		e.printStackTrace();
        	} finally {
        		if (buffer != null) { buffer.close(); }
        		inputStream.close();
        	}
        }
    	
    	return result;
    }

    /**
     * Creates a short array from a given byte array.
     * @param dataByte the array of bytes.
     * @return the array of shorts.
     */
	public static short[] byteArrayToShortArray(byte[] dataByte) {
		int size = dataByte.length;
		short[] shortArray = new short[size];
		
		for (int i = 0; i < size; i++) {
			//shortArray[i] = dataByte[i];
			shortArray[i] = (short)(dataByte[i] & 0xff);
		}
		return shortArray;
	}
	
	/**
	 * Opens the given input stream and unpack it if it contains a LHA file. Else, returns an array of bytes.
	 * @param is the input stream of a LHA file, or of a normal file.
	 * @return an array of bytes.
	 * @throws IOException 
	 */
	public static byte[] unpackLHAFile(InputStream is) throws IOException {
		
		/*
		// CRASHES if not a LHA file ! We'll have to open the stream, test it, if not good, close it, open a new one, get the array.
		LhaInputStream lis = new LhaInputStream(is);

		byte[] data = null;
		if (lis.getNextEntry() != null) {
			data = readInputStream(lis);
		}
		*/
		
		byte[] data = readInputStream(is);		
		//is.mark(0);
		
		//byte[] header = new byte[length];
		//int nbBytesRead = is.read(data, 0, length);
		
		// Test a (relevant ??) header.
		if ((data.length > 4) && ((data[3] == 'l') && (data[4] == 'h'))) {
			// The file "should" be a LHA file.
			is.reset();
			LhaInputStream lis = new LhaInputStream(is);

			if (lis.getNextEntry() != null) {
				data = readInputStream(lis);
			}	
		}
		
		
		return data;
	}
	
	
	
	public static byte[] unpackLHAFile(File musicFile) throws IOException {
		InputStream is = null;
		
		LhaFile lhaFile = new LhaFile(musicFile);
		LhaHeader[] headers = lhaFile.getEntries();
		// Does it contain a LHA file ? If not, it may be because it's not a LHA file !
		if (headers.length > 0) {
			LhaHeader header = headers[0];
			is = lhaFile.getInputStream(header);
		} else {
			// Probably not a LHA file. We get the bytes anyway.
			is = new FileInputStream(musicFile);
		}
		
		
		
		byte[] data = readInputStream(is);		
		//is.mark(0);
		
		//byte[] header = new byte[length];
		//int nbBytesRead = is.read(data, 0, length);
		
		// Test a (relevant ??) header.
//		if ((data.length > 4) && ((data[3] == 'l') && (data[4] == 'h'))) {
//			// The file "should" be a LHA file.
//			is.reset();
//			LhaInputStream lis = new LhaInputStream(is);
//
//			if (lis.getNextEntry() != null) {
//				data = readInputStream(lis);
//			}	
//		}
		
		
		return data;
	}
	
}
