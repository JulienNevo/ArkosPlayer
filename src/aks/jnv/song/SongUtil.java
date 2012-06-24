package aks.jnv.song;

import java.io.File;
import java.io.InputStream;

import aks.jnv.reader.AKSSongReader;
import aks.jnv.reader.ISongReader;
import aks.jnv.reader.YMSongReader;
import aks.jnv.util.Util;

/**
 * Utility Class for Song. Most notably, the ability to get the type of a Song according to its raw (binary) data.
 *  
 * @author Julien Névo
 *
 */
public class SongUtil {

	
	
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
		
		short[] data;
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
	public static String readNTString(short[] data, int index) {
		StringBuilder sb = new StringBuilder(10);
		
		int size = data.length;
		boolean finished = false;
		
		while ((!finished) && (index < size)) {
			short val = data[index];
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
	 * @param data the array to read.
	 * @param index the index of the NT-String.
	 * @return the WORD.
	 */
	public static int readWord(short[] data, int index) {
		return (data[index++] << 8) + data[index];
	}
	
	/**
	 * Reads a DWORD (32 bits) from the given array, big-endian.
	 * @param data the array to read.
	 * @param index the index of the NT-String.
	 * @return the DWORD.
	 */
	public static int readDWord(short[] data, int index) {
		return (data[index++] << 24) + (data[index++] << 16) + (data[index++] << 8) + data[index];
	}
	
}
