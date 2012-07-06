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
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * This class is a Thread that sends a buffer to the sound card whenever it requires it, thanks to the blocking
 * "write" method of the Media API.
 * 
 * The generation of the buffer is done through the generateAudioBuffer method of the audioBufferGenerator
 * object that should be set before using the Renderer.
 * 
 * @author Julien Névo
 *
 */
public class AudioRenderer extends Thread {

	/** The Audio Buffer Generator that will fill the buffer before we send it to the sound card. */
	private IAudioBufferGenerator audioBufferGenerator;
	
	/** The AudioTrack used to perform the sound. */
	private AudioTrack audioTrack;
	
	/** Sample rate in Hz. */
	private int sampleRate = 22050; //16000		FIXME: CPU doesn't lower even at 11025... ????
	
	/** Bit rate (8, 16 bits). */
	//private int bitRate = 16;
	
	/** Channel count (1 for mono). */
	private int nbChannels = 2;
	
	
	
	
	/** Indicates if the AudioTrack has been initialized. */
	private boolean isInitialized;
	
	/** Size in bytes of the output buffer. */
	private int outputBufferSize;
	
	/** The outputBuffer to fill and sent to the AudioTrack. */
	private short[] outputBuffer;
	
	
	
	
	
	/** Indicates whether the thread has started.*/
	private boolean threadStarted;
	
	/** Indicates whether the thread has to stop.*/
	private boolean threadMustStop;
	
	/** Indicates if the AudioTrack is ready to be filled.*/
	private boolean readyToBeFilled;
	
	
	
	// ***************************************
	// Getters and setters
	// ***************************************
	
	/**
	 * Sets the Audio Buffer Generator. It must be done before setting the Song Reader.
	 */
	public void setAudioBufferGenerator(IAudioBufferGenerator audioBufferGenerator) {
		this.audioBufferGenerator = audioBufferGenerator;
	}

//	public IAudioBufferGenerator getAudioBufferGenerator() {
//		return audioBufferGenerator;
//	}
	
	/**
	 * Sets the Song Reader to be used by the AudioBufferGenerator. It must be done after the AudioBufferGenerator sampleRate has been set.
	 * @param songReader the Song reader to use.
	 */
	public void setSongReader(ISongReader songReader) {
		if (audioBufferGenerator != null) {
			audioBufferGenerator.initialize(songReader);
		}
	}
	
	/**
	 * Returns the sample rate used to render the song.
	 * @return the sample rate used to render the song.
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	
	// ***************************************
	// Public methods
	// ***************************************

    /**
     * Starts playing the sound.
     */
	public void startSound() {
		if (threadStarted || !audioBufferGenerator.isReady()) {
			return;
		}
		
		initialize();
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		audioTrack.flush();
		audioTrack.play();
		
		super.start();
		
	}
	
	/**
     * Stops the sound.
     */
    public void stopSound() {
    	if (isInitialized) {
    		threadMustStop = true;
    		isInitialized = false;
    		
    		try {
				join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
    		//super.stop();
    	}
    }
	
	@Override
	public void run() {
		threadStarted = true;
		
		if (!isInitialized) {
			initialize();
		}
		
		// "Infinite loop", generating and playing the sounds as long as needed.
		while (!threadMustStop) {
			if (!readyToBeFilled) {
				// Makes sure the AudioTrack is ready to receive our data !
				readyToBeFilled = (audioTrack.getState() == AudioTrack.STATE_INITIALIZED);
			} else {
				// Fills the output buffer with our generated samples.
				generateBuffer();
				audioTrack.write(outputBuffer, 0, outputBufferSize);
			}
		}
		
		threadStarted = false;
		
		audioTrack.flush();
		audioTrack.stop();
		audioTrack.release();
		audioTrack = null;
		isInitialized = false;
	}
	
	/**
	 * Set a new seek position in seconds. It may not be set accurately, according to the format of the song.
	 * @param seconds the new seek position in seconds.
	 */
	public void seek(int seconds) {
		if (audioBufferGenerator != null) {
			audioBufferGenerator.seek(seconds);
		}
	}
	
	
	/**
	 * Initializes the AudioRenderer. Instantiates the AudioTrack. Must be performed in order the sound to be played.
	 * Must be performed again after the sound has been stopped.
	 */
	private void initialize() {
		if (!isInitialized) {
			int nbChannelsFlag = (nbChannels == 1) ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
			outputBufferSize = AudioTrack.getMinBufferSize(sampleRate, nbChannelsFlag, AudioFormat.ENCODING_PCM_16BIT);
			
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, nbChannelsFlag,
					AudioFormat.ENCODING_PCM_16BIT, outputBufferSize, AudioTrack.MODE_STREAM);
			
			// Creates a new buffer, unless it's not necessary.
			if ((outputBuffer == null) || ((outputBuffer != null) && (outputBuffer.length != outputBufferSize))) {
				outputBuffer = new short[outputBufferSize];
			}
			
			clearBuffer();
			
			isInitialized = true;
			threadMustStop = false;
		}
	}

	/**
	 * Clears the buffer.
	 */
	private void clearBuffer() {
		for (int i = 0; i < outputBufferSize; ++i) {
			outputBuffer[i] = 0;
		}
	}

	/**
	 * Fills the outputBuffer with a generated sound.
	 */
	private void generateBuffer() {
		if ((outputBuffer == null) || (audioBufferGenerator == null)) {
			return;
		}
		
		audioBufferGenerator.generateAudioBuffer(outputBuffer);
	}
	
}
