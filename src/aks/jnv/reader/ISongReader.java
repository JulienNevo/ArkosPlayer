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

import aks.jnv.audio.IEqualizerObserver;
import aks.jnv.audio.ISeekPositionObserver;

/**
 * This interface suits any Reader for them to provide the registers of each frame of the song, whenever
 * the getRegisters method is called.
 * 
 * The getRegisters method is called when a new frame must be read in the song. It is called by the IAudioBufferGenerator.
 * 
 * The readSongInformation method MUST be called once after the song has been loaded into the Reader.
 * 
 * @author Julien Névo
 *
 */
public interface ISongReader {
	
	/**
	 * Reads the Song information by reading the header of its data.
	 */
	//private void readSongInformation();
	
	/**
	 * Returns the registers of the next frame of the song.
	 * @return the registers of the next frame of the song.
	 */
	int[] getNextRegisters();
	
	/**
	 * Seeks to the given second in the song.
	 * @param secondToReach the second to reach.
	 * @return the actual second of the song reached. It is not guaranteed that it will be equal to the second
	 * requested.
	 */
	//int seekInSeconds(int secondToReach);
	
	/**
	 * Returns the replay frequency of the song in Hz (50hz, 100hz...).
	 * @return the replay frequency of the song in Hz (50hz, 100hz...).
	 */
	int getReplayFrequency();
	
	/**
	 * Returns the PSG frequency used by the song in Hz (1Mhz...).
	 * @return the PSG frequency used by the song in Hz (1Mhz...).
	 */
	int getPSGFrequency();
	
	/**
	 * Returns the sample whose number if given. Returns Null is no sample could be found.
	 * @param sampleNumber the zero-based index of the sample.
	 * @return the sample, or Null.
	 */
	byte[] getSample(int sampleNumber);
	
	
	/**
	 * Returns the name of the song.
	 * @return the name of the song.
	 */
	String getName();
	
	/**
	 * Returns the author of the song.
	 * @return the author of the song.
	 */
	String getAuthor();
	
	/**
	 * Returns the comments of the song.
	 * @return the comments of the song.
	 */
	String getComments();
	
	/**
	 * Returns the duration of the song in seconds.
	 * @return the duration of the song in seconds.
	 */
	int getDuration();

	/**
	 * Returns the format of the song.
	 * @return the format of the song.
	 */
	String getFormat();

	/**
	 * Sets a new seek position in seconds. It may not be set accurately, according to the format of the song.
	 * @param seconds the new seek position in seconds.
	 */
	void seek(int seconds);
	
	/**
	 * Adds a seek position observer. It will be notified whenever the seek position changes.
	 * @param observer the seek position observer.
	 */
	void addSeekObserver(ISeekPositionObserver observer);
	
	/**
	 * Adds an equalizer observer. It will be notified whenever the equalizer values changes.
	 * @param observer The equalizer observer.
	 */
	void addEqualizerObserver(IEqualizerObserver observer);
	
	/**
	 * Removes a seek position observer.
	 * @param observer The observer.
	 */
	void removeSeekObserver(ISeekPositionObserver observer);
	
	/**
	 * Removes an equalizer position observer.
	 * @param observer The observer.
	 */
	void removeEqualizerObserver(IEqualizerObserver observer);
	
	/**
	 * Returns the volume value of the given channel.
	 * @param channel the channel (>0).
	 * @return the volume of the given channel (0<=volume<=16).
	 */
	int getVolumeChannel(int channel);
	
	/**
	 * Returns the noise value.
	 * @return the noise value.
	 */
	int getNoiseValue();
	
	/**
	 * Returns the channels that use the noise, as a mask.
	 * @return the channels that use the noise (bit 0 = first channel etc.).
	 */
	int getNoiseChannels();
}
