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
	
	

	/** Folder inside the DIRECTORY_MUSIC where are the music.*/
	//public static final String YM_FOLDER = "/YM/"; 
	
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
}
