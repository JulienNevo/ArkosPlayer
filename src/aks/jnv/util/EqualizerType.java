package aks.jnv.util;

import aks.jnv.R;
import android.content.Context;

/**
 * Enumeration for the equalizer type.
 * 
 * @author Julien Névo
 *
 */
public enum EqualizerType {
	EQUALIZER_3D,
	EQUALIZER_2D,
	EQUALIZER_NONE;

	/**
	 * Converts the given equalizer type as a String to the matching enumeration. If none matches, the "none" type is returned.
	 * @param context A Context.
	 * @param typeString The equalizer type.
	 * @return The matching enumeration.
	 */
	public static EqualizerType getEqualizerFromString(Context context, String typeString) {
		EqualizerType result;
		
		if (typeString.equals(context.getString(R.string.preferences_equalizer_type_3d))) {
			result = EQUALIZER_3D;
		} else if (typeString.equals(context.getString(R.string.preferences_equalizer_type_3d))) {
			result = EQUALIZER_2D;
		} else {
			result = EQUALIZER_NONE;
		}
		
		return result;
	}
}
