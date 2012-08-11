/*
 * Copyright (c) 2012 Julien Névo. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *  * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *  * The names of the authors or their institutions shall not
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package aks.jnv.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.lhadecompressor.LhaEntry;
import net.sourceforge.lhadecompressor.LhaFile;

import android.util.Log;

/**
 * Takes care of accessing the SD card, looking for the music files, accessing them.
 * 
 * @author Julien Névo
 *
 */
public class FileUtils {

	/** The debug tag of this class. */
	private static final String DEBUG_TAG = "FileUtils";
	
 	/** Folder where is the music. */
	//public static final String MUSIC_FOLDER = "/mnt/sdcard/Music/YM/";
	//public static final String MUSIC_FOLDER = "/mnt/sdcard/Music/YM/Tunes/Follin.Bros/";
	public static final String MUSIC_FOLDER = "/mnt/sdcard/Music/YM/Tunes/Whittacker.David/";

	/** Dot character such as the ones found in file extensions. */
	private static final char DOT_CHAR = '.'; 
	
//	/**
//	 * Indicates if an external storage is available for reading (or read/write).
//	 * @return true if an external storage is available for reading (or read/write).
//	 */
//	public static boolean isExternalStorageAvailableForReading() {
//		String state = Environment.getExternalStorageState();
//		return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
//	}
//
//	/**
//	 * Returns the music folder, or Null if no device or no folder could be found.
//	 * @param context the context.
//	 * @return a File of the folder where the music are, or Null if no device or the folder could be found.
//	 */
//	public static File getMusicFolder(Context context) {
//		return isExternalStorageAvailableForReading() ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) : null;
//	}

	/**
	 * Returns the music short name from its full path. Extension removed at will.
	 * @param path The song full path.
	 * @param keepExtension True to keep the extension.
	 * @return The music short name.
	 */
	public static String getMusicShortName(String path, boolean keepExtension) {
		int lastSlashIndex = path.lastIndexOf(File.separatorChar);
		
		// Finds the last slash, and skips it.
		int firstIndex = (lastSlashIndex >= 0) ? (lastSlashIndex + 1) : 0;

		int lastIndexExcluded;
		if (keepExtension) {
			// If the extension is kept, gets the remaining of the String. 
			lastIndexExcluded = path.length();
		} else {
			// If the extension is removed, finds the last dot.
			lastIndexExcluded = path.lastIndexOf(DOT_CHAR);
			// Security in case the dot isn't found (also takes care of the (rather unlikely) possibility that the last dot is before the last slash).
			if (lastIndexExcluded < firstIndex) {
				lastIndexExcluded = path.length();
			}
		}
		
		return path.substring(firstIndex, lastIndexExcluded);
	}

	/**
	 * Returns the string without the extension, if any.
	 * @param filename The filename.
	 * @return The string without the extension, if any.
	 */
	public static String removeExtension(String filename) {
		int dotIndex = filename.lastIndexOf(DOT_CHAR);
		if (dotIndex < 0) {
			dotIndex = filename.length();
		}
		return filename.substring(0, dotIndex);
	}
	
	
	
	
	/** The size of the buffer for reading files. */
	private static final int BUFFER_SIZE = 16384;
	
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
	        	
	        	byte[] dataRead = new byte[BUFFER_SIZE];
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
//	public static byte[] unpackLHAFile(InputStream is) throws IOException {
//		
//		/*
//		// CRASHES if not a LHA file ! We'll have to open the stream, test it, if not good, close it, open a new one, get the array.
//		LhaInputStream lis = new LhaInputStream(is);
//
//		byte[] data = null;
//		if (lis.getNextEntry() != null) {
//			data = readInputStream(lis);
//		}
//		*/
//		
//		byte[] data = readInputStream(is);		
//		//is.mark(0);
//		
//		//byte[] header = new byte[length];
//		//int nbBytesRead = is.read(data, 0, length);
//		
//		// Test a (relevant ??) header.
//		if ((data.length > 4) && ((data[3] == 'l') && (data[4] == 'h'))) {
//			// The file "should" be a LHA file.
//			is.reset();
//			LhaInputStream lis = new LhaInputStream(is);
//
//			if (lis.getNextEntry() != null) {
//				data = readInputStream(lis);
//			}	
//		}
//		
//		
//		return data;
//	}
	
	
	/**
	 * Unpacks the given LHA file. If the file isn't present or isn't a LHA file, nothing happens.
	 * @param musicFile The file to read.
	 * @return a byte buffer of the uncompressed data, or null.
	 * @throws Exception
	 */
	public static byte[] unpackLHAFile(File musicFile) throws Exception {
		InputStream is = null;
		
		LhaFile lhaFile = null;
		byte[] data = null;
		
		try {
			lhaFile = new LhaFile(musicFile);
			LhaEntry entry = lhaFile.getEntry(0);
			//int length = (int)entry.getOriginalSize();
			is = new BufferedInputStream(lhaFile.getInputStream(entry), BUFFER_SIZE);
			data = readInputStream(is);
			//int nbBytesRead = is.read(data, 0, length);
		} catch (Exception e) {
			Log.e(DEBUG_TAG, "Error while opening the possible LHA file.");
			data = null;
		}
		
		return data;
	}
	
	
	
}
