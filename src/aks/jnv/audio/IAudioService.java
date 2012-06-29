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

package aks.jnv.audio;

import java.io.File;

/**
 * Audio Service Interface used by the Audio Service, in order to be controlled by whatever uses the Service.
 * 
 * @author Julien Névo
 *
 */
public interface IAudioService {
	
	/**
	 * Sets the song to be played. It can be of any playable format.
	 * @param song the song to be played.
	 * @return true if the file is recognized as a playable song.
	 */
	boolean setSong(File song);
	
	/**
	 * Stops the song. This has the effect of making the seek to the beginning of the song.
	 */
	void stop();
	
	/**
	 * Pauses the song.
	 */
	void pause();
	
	/**
	 * Plays/resume the song.
	 */
	void play();

	/**
	 * Seeks to a specific position. According to the format of the song being played, it is not guaranteed that
	 * the specific second will be reached.
	 * @param seconds the second to be reached.
	 */
	void seek(int seconds);
	
	/**
	 * Returns the song information of the current song. Returns Null if no song if song is loaded.
	 * @return the song information of the current song, or Null.
	 */
	SongInformation getSongInformation();
	
	/**
	 * Returns the current seek position of the current song, in seconds. Returns 0 if no song if song is loaded.
	 * @return the current seek position of the current song, in seconds, or 0.
	 */
	int getCurrentSeekPosition();
	
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
