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

	/** The debug tag of this class. */
	public static final String DEBUG_TAG = AudioRenderer.class.getSimpleName();
	
	/** Sample rate in Hz. */
	private static final int SAMPLE_RATE = 44100;
	
	/** The Audio Buffer Generator that will fill the buffer before we send it to the sound card. */
	private IAudioBufferGenerator mAudioBufferGenerator;
	
	/** The AudioTrack used to perform the sound. */
	private AudioTrack mAudioTrack;
	
	/** Indicates if the AudioTrack has been initialized. */
	private boolean mIsInitialized;
	
	/** Size in bytes of the output buffer. */
	private int mOutputBufferSize;
	
	/** The outputBuffer to fill and sent to the AudioTrack. */
	private short[] mOutputBuffer;
	
	/** Indicates whether the thread has started.*/
	private boolean mThreadStarted;
	/** Indicates whether the thread has to stop.*/
	private boolean mThreadMustStop;
	
	/** Indicates if the AudioTrack is ready to be filled.*/
	private boolean mReadyToBeFilled;
	
	
	
	// ***************************************
	// Getters and setters
	// ***************************************
	
	/**
	 * Sets the Audio Buffer Generator. It must be done before setting the Song Reader.
	 */
	public void setAudioBufferGenerator(IAudioBufferGenerator audioBufferGenerator) {
		this.mAudioBufferGenerator = audioBufferGenerator;
	}

	/**
	 * Sets the Song Reader to be used by the AudioBufferGenerator. It must be done after the AudioBufferGenerator sampleRate has been set.
	 * @param songReader the Song reader to use.
	 */
	public void setSongReader(ISongReader songReader) {
		if (mAudioBufferGenerator != null) {
			mAudioBufferGenerator.initialize(songReader);
		}
	}
	
	/**
	 * Returns the sample rate used to render the song.
	 * @return the sample rate used to render the song.
	 */
	public int getSampleRate() {
		return SAMPLE_RATE;
	}

	
	// ***************************************
	// Public methods
	// ***************************************

    /**
     * Starts playing the sound.
     */
	public void startSound() {
		if (mThreadMustStop || mThreadStarted || !mAudioBufferGenerator.isReady()) {
//			Log.e(DEBUG_TAG, "Thread won't start because not the right conditions.");
//			Log.e(DEBUG_TAG, "ThreadMustStop = " + threadMustStop);
//			Log.e(DEBUG_TAG, "threadStarted = " + threadStarted);
//			Log.e(DEBUG_TAG, "audioBufferGenerator.isReady() = " + audioBufferGenerator.isReady());
			return;
		}
		
		initialize();
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		mAudioTrack.flush();
		mAudioTrack.play();
		
		super.start();
		
	}
	
	/**
     * Stops the sound.
     */
    public void stopSound() {
    	if (mIsInitialized) {
    		mThreadMustStop = true;
    		//isInitialized = false;
    		
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
		//Log.e(DEBUG_TAG, "Thread.run");
		
		mThreadStarted = true;
		
		if (!mIsInitialized) {
			//initialize();
			//Log.e(DEBUG_TAG, "Thread not initialized.");
			return;
		}
		
		// "Infinite loop", generating and playing the sounds as long as needed.
		while (!mThreadMustStop) {
			if (!mReadyToBeFilled) {
				// Makes sure the AudioTrack is ready to receive our data !
				mReadyToBeFilled = (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED);
			} else {
				// Fills the output buffer with our generated samples.
				generateBuffer();
				mAudioTrack.write(mOutputBuffer, 0, mOutputBufferSize);
			}
		}
		
		mThreadStarted = false;
		mThreadMustStop = false;
		
		mAudioTrack.flush();
		mAudioTrack.stop();
		mAudioTrack.release();
		mAudioTrack = null;
		mIsInitialized = false;
		
		//Log.e(DEBUG_TAG, "Thread is dead.");
	}
	
	/**
	 * Set a new seek position in seconds. It may not be set accurately, according to the format of the song.
	 * @param seconds the new seek position in seconds.
	 */
	public void seek(int seconds) {
		if (mAudioBufferGenerator != null) {
			mAudioBufferGenerator.seek(seconds);
		}
	}
	
	
	/**
	 * Initializes the AudioRenderer. Instantiates the AudioTrack. Must be performed in order the sound to be played.
	 * Must be performed again after the sound has been stopped.
	 */
	private void initialize() {
		if (!mIsInitialized) {
			mOutputBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
			
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, mOutputBufferSize, AudioTrack.MODE_STREAM);
			
			// Creates a new buffer, unless it's not necessary.
			if ((mOutputBuffer == null) || ((mOutputBuffer != null) && (mOutputBuffer.length != mOutputBufferSize))) {
				mOutputBuffer = new short[mOutputBufferSize];
			}
			
			clearBuffer();
			
			mIsInitialized = true;
			mThreadMustStop = false;
		}
	}

	/**
	 * Clears the buffer.
	 */
	private void clearBuffer() {
		for (int i = 0; i < mOutputBufferSize; ++i) {
			mOutputBuffer[i] = 0;
		}
	}

	/**
	 * Fills the outputBuffer with a generated sound.
	 */
	private void generateBuffer() {
		if ((mOutputBuffer == null) || (mAudioBufferGenerator == null)) {
			return;
		}
		
		mAudioBufferGenerator.generateAudioBuffer(mOutputBuffer);
	}
	
}
