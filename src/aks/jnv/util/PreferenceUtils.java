package aks.jnv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Helper class that stores and retrieves the data stored in the Shared Preferences.
 * 
 * @author Julien Névo
 *
 */
public class PreferenceUtils {

	/** File name where the preferences are stored. */
	private static final String PREFERENCE_FILE = "PreferenceFile";
	
	/** Key to the current music folder. */
	private static final String KEY_CURRENT_MUSIC_FOLDER = "CurrentMusicFolder";
	
	/**
	 * Stores the current music folder.
	 * @param musicFolder The current music folder.
	 */
	public static void setCurrentMusicFolder(Context context, String musicFolder) {
		storeStringValue(context, musicFolder, KEY_CURRENT_MUSIC_FOLDER);
	}

	/**
	 * Returns the possibly stored music folder, or null if none was stored.
	 * @param context A Context.
	 * @return The possibly stored music folder, or null if none was stored.
	 */
	public static String getCurrentMusicFolder(Context context) {
		return restoreStringValue(context, KEY_CURRENT_MUSIC_FOLDER, null);
	}
	
	
	// ---------------
	// Private methods
	// ---------------

	/**
	 * Stores the given String value inside the given key of the Shared Preferences file of the application.
	 * @param context A Context.
	 * @param value The value to store.
	 * @param key The key.
	 */
	private static void storeStringValue(Context context, String value, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * Returns the possibly stored String inside the given key of the Shared Preferences file of the application.
	 * @param context A Context.
	 * @param key The key.
	 * @param defaultValue The value returned if no key was stored.
	 * @return The value, or the default value if no key was stored.
	 */
	private static String restoreStringValue(Context context, String key, String defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, defaultValue);
	}
	
}
