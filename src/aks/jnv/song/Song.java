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

/**
 * Represents a Song, whatever format it is.
 * 
 * @author Julien Névo
 *
 */
public class Song {

	/** The data of the song. */
	private byte[] mData;

	/** The replay frequency of the song, in Hz (50hz, 25hz etc.). */
	private int mReplayFrequency;
	/** The frequency of the sound processor for which the song has been made, in Hz. */
	private int mPSGFrequency;
	/** The name of the song. May be Null. */
	private String mSongName;
	/** The author of the song. May be Null. */
	private String mAuthor;
	/** The comments of the song. May be Null. */
	private String mComments;
	
	/**
	 * Constructor of a song.
	 * @param data The data of the song.
	 * @param replayFrequency The replay frequency of the song, in Hz (50hz, 25hz etc.).
	 * @param PSGFrequency The frequency of the sound processor for which the song has been made, in Hz.
	 * @param name The name of the song. May be Null.
	 * @param author The author of the song. May be Null.
	 * @param comments The comments of the song. May be Null.
	 */
	public Song(byte[] data, int replayFrequency, int PSGFrequency, String name, String author, String comments) {
		mData = data;
		mReplayFrequency = replayFrequency;
		mPSGFrequency = PSGFrequency;
		mSongName = name;
		mAuthor = author;
		mComments = comments;
	}
	
	
	// ***************************************
	// Getters and setters
	// ***************************************
	
	/**
	 * Returns the replay frequency of the song, in Hz (50hz, 25hz etc.).
	 * @return the replay frequency of the song, in Hz.
	 */
	public int getReplayFrequency() {
		return mReplayFrequency;
	}

	/**
	 * Returns the frequency of the sound processor for which the song has been made, in Hz.
	 * @return the frequency of the sound processor for which the song has been made, in Hz.
	 */
	public int getPSGFrequency() {
		return mPSGFrequency;
	}

	/**
	 * Returns the name of the song.
	 * @return the name of the song.
	 */
	public String getName() {
		return mSongName;
	}

	/**
	 * Returns the author of the song.
	 * @return the author of the song.
	 */
	public String getAuthor() {
		return mAuthor;
	}

	/**
	 * Returns the comments of the song.
	 * @return the comments of the song.
	 */
	public String getComments() {
		return mComments;
	}
	
	/**
	 * Returns the data of the songs.
	 * @return the data of the songs.
	 */
	public byte[] getData() {
		return mData;
	}

}
