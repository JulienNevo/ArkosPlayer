package aks.jnv.util;

import aks.jnv.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Helper class that stores and retrieves the data stored in the Shared Preferences.
 * 
 * @author Julien Névo
 *
 */
public class PreferenceUtils {

	
	// -----------------------------------------------------------------
	// Public methods.
	// -----------------------------------------------------------------
	
	/**
	 * Stores the current music folder <b>from the external storage root</b>.
	 * @param musicFolder The current music folder.
	 */
	public static void setCurrentMusicFolder(Context context, String musicFolder) {
		storeStringValue(context, musicFolder, context.getString(R.string.preferences_key_music_path));
	}

	/**
	 * Returns the possibly stored music folder <b>from the external storage root</b>, or null if none was stored.
	 * @param context A Context.
	 * @return The possibly stored music folder, or null if none was stored.
	 */
	public static String getCurrentMusicFolder(Context context) {
		return restoreStringValue(context, context.getString(R.string.preferences_key_music_path), null);
	}
	
	/**
	 * Returns the current equalizer type.
	 * @param context A Context.
	 * @return The type of the equalizer.
	 */
	public static EqualizerType getEqualizerType(Context context) {
		String typeString = restoreStringValue(context, context.getString(R.string.preferences_key_equalizer), context.getString(R.string.preferences_equalizer_type_none));
		return EqualizerType.getEqualizerFromString(context, typeString);
	}
	
	/**
	 * Indicates whether the equalizer is used.
	 * @param context A Context.
	 * @return
	 */
	public static boolean isEqualizerUsed(Context context) {
		return getEqualizerType(context) != EqualizerType.EQUALIZER_NONE;
	}

	
	// -----------------------------------------------------------------
	// Private methods.
	// -----------------------------------------------------------------

	/**
	 * Stores the given String value inside the given key of the Shared Preferences file of the application.
	 * @param context A Context.
	 * @param value The value to store.
	 * @param key The key.
	 */
	private static void storeStringValue(Context context, String value, String key) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key, defaultValue);
	}
}
