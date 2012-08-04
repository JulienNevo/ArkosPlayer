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
package aks.jnv.adapter;

import aks.jnv.file.FileManager;
import aks.jnv.song.SongFormat;

/**
 * Very simple class holding information about an item of the music selection Activity.
 * 
 * The comparison is based on the short name of the music, in lower case. This short name in lower case is cached in order to speed the process.
 * 
 * @author Julien Névo
 *
 */
public class MusicItem implements Comparable<MusicItem> {
	
	/** The path of the music file. */
	private String mPath;
	/** The song format. */
	private SongFormat mSongFormat;
	/** The music short name, with the extension. It is stored for convenience. As a substring from the path, it shouldn't cost much. */
	private String mShortName;
	/** The music short name, with the extension and in lower case. It is stored because the sorting is based on it. It's faster to cache it. */
	private String mShortNameLowerCase;
	
	/**
	 * Constructor.
	 * @param path The path of the music file.
	 * @param songFormat The song format.
	 */
	public MusicItem(String path, SongFormat songFormat) {
		mPath = path;
		mSongFormat = songFormat;
		mShortName = FileManager.getMusicShortName(mPath, true);
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
	 * Returns the song format.
	 * @return The song format.
	 */
	public SongFormat getSongFormat() {
		return mSongFormat;
	}

	/**
	 * Returns the short name of the music, with the extension.
	 * @return The short name of the music, with the extension.
	 */
	public String getShortName() {
		return mShortName;
	}


	@Override
	public int compareTo(MusicItem another) {
		if (another == this) {
			return 0;
		}

		// The comparison is based on the short name in lower case.
		return mShortNameLowerCase.compareTo(another.mShortNameLowerCase);
	}
}
