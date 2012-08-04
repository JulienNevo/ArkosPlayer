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

package aks.jnv.file;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * Takes care of accessing the SD card, looking for the music files, accessing them.
 * 
 * @author Julien Névo
 *
 */
public class FileManager {

	/** The music list found. May be Null or empty. */
	private static File[] musicList;
	
	/** Folder we are the music. */
	public static final String MUSIC_FOLDER = "/mnt/sdcard/Music/YM/";

	/** Dot character such as the ones found in file extensions. */
	private static final char DOT_CHAR = '.'; 
	
	/**
	 * Indicates if an external storage is available for reading (or read/write).
	 * @return true if an external storage is available for reading (or read/write).
	 */
	public static boolean isExternalStorageAvailableForReading() {
		String state = Environment.getExternalStorageState();
		return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
	}

	/**
	 * Returns the music folder, or Null if no device or no folder could be found.
	 * @param context the context.
	 * @return a File of the folder where the music are, or Null if no device or the folder could be found.
	 */
	public static File getMusicFolder(Context context) {
		return isExternalStorageAvailableForReading() ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) : null;
		//return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC); // + YM_FOLDER);
		//return context.getExternalFilesDir(null);
	}

	/**
	 * Builds the list of the music file available, and returns the number of music. May be Null if no device, no music folder
	 * or no music could be found.
	 * @param context the context.
	 * @return the number of music found.
	 */
	public static int buildMusicFileList(Context context) {
		// Music List is cached. No need to fill it more than once.
		if (musicList == null) {
			File musicFolder = FileManager.getMusicFolder(context);
			if (musicFolder != null) {
				musicList = musicFolder.listFiles();
			}
		}
		
		return (musicList != null ? musicList.length : 0);
	}
	
	/**
	 * Returns the music whose index is given.
	 * @param musicNumber the music number.
	 * @return the music, or Null if the music doesn't exist.
	 */
	public static File getMusic(int musicNumber) {
		File music = null;
		
		if ((musicList != null) && (musicNumber < musicList.length)) {
			music = musicList[musicNumber];
		}
		
		return music;
	}
	
	/**
	 * Returns the music list. It must have been built before. May be Null or empty
	 * @return the music list, empty or not, or Null.
	 */
	public static File[] getMusicList() {
		return musicList;
	}

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
}
