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

package aks.jnv.reader;

import java.util.ArrayList;
import java.util.List;

import aks.jnv.audio.ISeekPositionObserver;
import aks.jnv.song.Song;

/**
 * Reader of an AKS binary song (Arkos Tracker).
 * 
 * This class is only boiler-plate at the moment.
 * 
 * @author Julien Névo
 *
 */
public class AKSSongReader implements ISongReader {

	private static final String UNKNOWN = "Unknown";
	private static final String FORMAT = "Arkos Tracker 1.0";

	/** The song being read.*/
	private Song song;
	
	//private short[] data;			// The data of the song.
	//private int dataPointer;		// Index to what is read in the data.

	private int replayFrequency;	// The replay frequency of the song, in Hz (50hz, 25hz etc.).
	private int PSGFrequency;		// The frequency of the sound processor for which the song has been made, in Hz.
	
	/** Observers of the seek position. They will be notified whenever it changes. */
	private List<ISeekPositionObserver> seekPositionObservers = new ArrayList<ISeekPositionObserver>();
	
	/**
	 * Constructor.
	 * @param song The AKSSong this reader must read.
	 */
//	public AKSSongReader(AKSSong song) {
//		this.song = song;
//	}
	
	/**
	 * Constructor of the SongReader. The Song MUST be in the correct format.
	 * The doesRawDataFit method must have been called before to ensure that.
	 * @param data the raw binary data of the song.
	 */
	public AKSSongReader(short[] data) {
		readSongInformation(data);
		song = new Song(data, replayFrequency, PSGFrequency, null, null, null);
	}
	
	// ***************************************
	// Getters and setters
	// ***************************************

	/**
	 * Returns the Song read by this Reader.
	 * @return the Song read by this Reader.
	 */
	public Song getSong() {
		return song;
	}

	@Override
	public int getReplayFrequency() {
		return replayFrequency;
	}

	@Override
	public int getPSGFrequency() {
		return PSGFrequency;
	}
	
	@Override
	public short[] getSample(int sampleNumber) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getName() {
		return UNKNOWN;
	}

	@Override
	public String getAuthor() {
		return UNKNOWN;
	}


	@Override
	public String getComments() {
		return UNKNOWN;
	}

	@Override
	public int getDuration() {
		// TODO GetDuration
		return 666;
	}

	@Override
	public String getFormat() {
		return FORMAT;
	}
	
	// ***************************************
	// Private methods
	// ***************************************

	/**
	 * Parses the given raw binary data and fills the field of the song.
	 * The Song MUST be in the correct format. The doesRawDataFit method must have been called before to ensure that.
	 * @param data the raw binary data of the song.
	 */
	private void readSongInformation(short[] data) {
		// TODO ***********************
	}
	

	// ***************************************
	// Public methods
	// ***************************************

	
	@Override
	public short[] getNextRegisters() {
		return null;
	}

	/**
	 * Indicates if the given raw binary data fits the format of the song.
	 * @param data the raw binary data of the song.
	 * @return true if the given raw binary data fits the format of the song.
	 */
	public static boolean doesRawDataFit(short[] data) {
		// FIXME
		return false;
	}

	@Override
	public void addSeekObserver(ISeekPositionObserver observer) {
		seekPositionObservers.add(observer);
	}

	@Override
	public void seek(int seconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getVolumeChannel(int channel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNoiseValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNoiseChannels() {
		// TODO Auto-generated method stub
		return 0;
	}




}
