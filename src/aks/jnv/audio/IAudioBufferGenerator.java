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

import aks.jnv.reader.ISongReader;

/**
 * Interface for any Class that would fill an audio buffer according to the registers it has access to. It works as in a "pull" mode.
 * While the generator fills its buffer, it can detect, according to the song replay frequency, if new registers must be pulled from
 * the song.
 * 
 * @author Julien Névo
 *
 */
public interface IAudioBufferGenerator {

	/**
	 * Fills the given buffer with a generated audio stream according to the registers it has access to, to be played.
	 * @param buffer the buffer to be filled.
	 */
	void generateAudioBuffer(short[] buffer);
	
	/**
	 * Sets the audio configuration. It is useful to produce the correct stream, but also to know when, in the buffer, new data
	 * should be streamed from the RegistersProvider.
	 * @param replayFrequency The replay frequency of the song, in Hz (50hz, 25hz etc.).
	 * @param PSGFrequency The frequency of the sound processor for which the song has been made, in Hz.
	 * @param sampleRate The sample rate at which the audio is output, in Hz (22050hz, 44100 etc.). 
	 */
	//void setAudioConfiguration(int replayFrequency, int PSGFrequency, int sampleRate);
	
	/**
	 * Initializes the AudioBufferGenerator. This MUST be done before it can generates data.
	 * The setSampleRate method MUST have been called before ! Also sets the SongReader to use,
	 * so that new registers can be read from it. This will also allow the AudioBufferGenerator to know
	 * what is the PSG frequency and the replay frequency of the song.
	 * @param songReader the SongReader to use.
	 */
	void initialize(ISongReader songReader);

	/**
	 * Sets the sample rate of the output, in Hz (44100, 22050 ...).
	 * @param sampleRate the sample rate of the output, in Hz.
	 */
	void setSampleRate(int sampleRate);

	/**
	 * Indicates if the Audio Buffer Generator is ready to play. It is ready if a song has been provided, as well as a sample rate.
	 * @return true if the Audio Buffer Generator is ready to play.
	 */
	boolean isReady();

	/**
	 * Set a new seek position in seconds. It may not be set accurately, according to the format of the song.
	 * @param seconds the new seek position in seconds.
	 */
	void seek(int seconds);
}
