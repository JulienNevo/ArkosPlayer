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

package aks.jnv.song;

import java.io.File;
import aks.jnv.reader.ISongReader;
import aks.jnv.reader.YMSongReader;

/**
 * Utility Class for Song. Most notably, the ability to get the type of a Song according to its raw (binary) data.
 *  
 * @author Julien Névo
 *
 */
public class SongUtil {

	/** The YM extension. */
	public static final String EXTENSION_YM = ".ym";
	
	
	/**
	 * Parses the header of the given raw song data in order to determine its format.
	 * @param data the data to parse.
	 * @return the format of the song.
	 */
	public static SongFormat getSongFormatFromRawData(short[] data) {
		// FIXME finish this code.
		return SongFormat.YM;
	}
	
	/**
	 * Creates a SongReader from its raw binary data. May return Null if no suitable format was found.
	 * @param data the raw data of the Song.
	 * @return an ISongReader, or Null if no known format could be found.
	 */
//	public static ISongReader getSongReaderFromRawData(short[] data) {
//		// TO REMOVE.
//		ISongReader songReader = null;
//		
//		if (YMSongReader.doesRawDataFit(data)) {
//			songReader = new YMSongReader(data);
//		} else if (AKSSongReader.doesRawDataFit(data)) {
//			songReader = new AKSSongReader(data);
//		}
//		
//		// FIXME finish when more format. Could be generalize (list of Class ?) ?
//		
//		return songReader;
//	}
	

//	public static ISongReader getSongReaderFromInputStream(InputStream is) {
//		ISongReader songReader = null;
//		
//		short[] data;
//		if ((data = YMSongReader.doesRawDataFit(is)) != null) {
//			songReader = new YMSongReader(data);
//		} //else if (AKSSongReader.doesRawDataFit(is)) {
//			//songReader = new AKSSongReader(is);
//		//}
//		
//		// FIXME finish when more format. Could be generalize (list of Class ?) ?
//		
//		return songReader;
//	}
	
	public static ISongReader getSongReaderFromFile(File musicFile) {
		ISongReader songReader = null;
		
		byte[] data;
		if ((data = YMSongReader.doesRawDataFit(musicFile)) != null) {
			songReader = new YMSongReader(data);
		} //else if (AKSSongReader.doesRawDataFit(is)) {
			//songReader = new AKSSongReader(is);
		//}
		
		// FIXME finish when more format. Could be generalize (list of Class ?) ?
		
		return songReader;
	}

	/**
	 * Builds an NT-String in the given array.
	 * @param data the array to read.
	 * @param index the index of the NT-String.
	 * @return the String.
	 */
	public static String readNTString(byte[] data, int index) {
		StringBuilder sb = new StringBuilder(10);
		
		int size = data.length;
		boolean finished = false;
		
		while ((!finished) && (index < size)) {
			int val = (data[index] & 0xff);
			if (val != 0) {
				sb.append((char)val);
				index++;
			} else {
				finished = true;
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Reads a WORD (16 bits) from the given array, big-endian.
	 * @param data The array to read.
	 * @param index The index of the NT-String.
	 * @return The WORD.
	 */
	public static int readWord(byte[] data, int index) {
		return ((data[index++] & 0xff) << 8) + (data[index] & 0xff);
	}
	
	/**
	 * Reads a DWORD (32 bits) from the given array, big-endian.
	 * @param data The array to read.
	 * @param index The index of the NT-String.
	 * @return The DWORD.
	 */
	public static int readDWord(byte[] data, int index) {
		return ((data[index++] & 0xff) << 24) + ((data[index++] & 0xff) << 16) + ((data[index++] & 0xff) << 8) + (data[index] & 0xff);
	}
	
	
	/**
	 * Indicates what format is the given file. It may be a music file or not!
	 * @param file The file.
	 * @return The song format, if any.
	 */
	public static SongFormat getFileFormat(File file) {
		SongFormat result = SongFormat.unknown;
		
		// FIXME For now, very simple detection: with the extension!
		if (file.getAbsolutePath().toLowerCase().endsWith(EXTENSION_YM)) {
			result = SongFormat.YM;
		}
		
		return result;
	}
	
	
	
	
}
