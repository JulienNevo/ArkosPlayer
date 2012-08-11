package aks.jnv.adapter;

import aks.jnv.util.FileUtils;

/**
 * An Item in the Music Selection. Only tells its path and if it's a folder of a music.
 * 
 * When comparing an item to another, if the other item is a music, this last one is considered of a higher order.
 * 
 * @author Julien Névo
 *
 */
public class MusicSelectionItem implements Comparable<MusicSelectionItem> {

	/** The path of the music file. */
	protected String mPath;
	/** True if the item is a folder. */
	protected boolean mIsFolder;
	/** The music short name, with the extension. It is stored for convenience. As a substring from the path, it shouldn't cost much. */
	private String mShortName;
	/** The music short name, with the extension and in lower case. It is stored because the sorting is based on it. It's faster to cache it. */
	private String mShortNameLowerCase;
	
	/**
	 * Constructor.
	 * @param path The path of the item (music or folder).
	 * @param isFolder true if the item is a folder.
	 */
	public MusicSelectionItem(String path, boolean isFolder) {
		mPath = path;
		mIsFolder = isFolder;
		mShortName = FileUtils.getMusicShortName(path, true);
		mShortNameLowerCase = mShortName.toLowerCase();
	}
	
	/**
	 * Returns the music path.
	 * @return The music path.
	 */
	public String getPath() {
		return mPath;
	}
	
	/**
	 * Returns true if the item is a folder.
	 * @return True if the item is a folder.
	 */
	public boolean isFolder() {
		return mIsFolder;
	}
	
	/**
	 * Returns the short name of the music, with the extension.
	 * @return The short name of the music, with the extension.
	 */
	public String getShortName() {
		return mShortName;
	}


	@Override
	public int compareTo(MusicSelectionItem another) {
		if (another == this) {
			return 0;
		}
		// If the current element is a folder and not the other one, the current one comes before (folders are before music items).
		if (mIsFolder && !another.mIsFolder) {
			return -1;
		}

		// The comparison is based on the short name in lower case.
		return mShortNameLowerCase.compareTo(another.mShortNameLowerCase);
	}
}
