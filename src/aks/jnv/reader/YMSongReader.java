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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aks.jnv.audio.IEqualizerObserver;
import aks.jnv.audio.ISeekPositionObserver;
import aks.jnv.song.Song;
import aks.jnv.song.SongUtil;
import aks.jnv.util.BinaryConstants;
import aks.jnv.util.FileUtils;
import android.util.Log;

/**
 * Reader of a YM song.
 * 
 * @author Julien Névo
 *
 */
public class YMSongReader implements ISongReader {

	/** The debug tag of this class. */
	private static final String DEBUG_TAG = YMSongReader.class.getSimpleName();
	
	/** The Atari ST PSG frequency. */
	private static final int ATARI_ST_PSG_FREQUENCY = 2000000;
	/** The default PSG frequency if it isn't given in the song header. */
	private static final int DEFAULT_PSG_FREQUENCY = ATARI_ST_PSG_FREQUENCY;
	/** The default replay frequency in case it isn't given in the song header. */
	private static final int DEFAULT_REPLAY_FREQUENCY = 50;
	
	//private static final int LEONARD_STRING_OFFSET = 4;			// Offset of the "LeOnArD!" string.
	private static final int NB_VALID_VBL_OFFSET = 12;
	private static final int SONG_ATTRIBUTES_OFFSET = 16;
	private static final int NB_DIGIDRUMS_OFFSET = 20;
	private static final int PSG_FREQUENCY_OFFSET = 22;			// Only from YM5 and up !
	private static final int REPLAY_FREQUENCY_OFFSET = 26;		// Only from YM5 and up !
	
	private static final int FRAME_LOOP_START_OFFSET = 24;
	private static final int FRAME_LOOP_START_OFFSET_YM5 = 28;	// Only from YM5 and up !
	
	//private static final String LEONARD_TAG = "LeOnArD!";
	
	private static final int DWORD_SIZE = 4;
	private static final int WORD_SIZE = 2;
	
	private static final int NB_REGISTERS_TILL_YM3 = 14;
	private static final int NB_REGISTERS_FROM_YM4 = 16;
	
	private static final int HEADER_TAG_SIZE = 4;				// Size in bytes of the Header Tag (YMxx).
	private static final int YM3B_LOOP_INFORMATION_SIZE = 4;	// YM3B have a four bytes loop start DWord at the end of the file.

	/** The song being read.*/
	private Song song;
	
	/** The data of the song. */
	private byte[] mData;
	
	/** The samples. May be Null. */
	private byte[][] samples;

	/** The replay frequency of the song, in Hz (50hz, 25hz etc.). */
	private int mReplayFrequency;
	/** The frequency of the PSG for which the song has been made, in Hz. */
	private int mPSGFrequency;
	/** The name of the song. */
	private String mSongName;
	/** The author of the song. */
	private String mAuthor;
	/** The comments of the song. */
	private String mComments;
	
	/** Indicates if the YM registers are encoded all one after one in each frame (false) or interlaced (true).*/
	private boolean mIsInterleaved;
	
	/** Indicates the version of the YM (1, 2, 6...). */
	private int mYMVersion;
	
	/** Valid VBL count in the song.*/
	private int mNbFrames;
	
	/** Indicates if the samples are signed. */
	private boolean mAreSamplesSigned;
	
	/** Indicates if the samples are already in ST 4 bits format. */
	private boolean mAreSamplesInST4BitsFormat;
	
	/** The sample count. */
	private int mNbSamples;
	
	/** The frame loop start. */
	private int mFrameLoopStart;
	
	/** Offset of the first register of the first frame of the song. */
	private int mRegistersBaseOffset;
	
	/** Current frame/VBL. */
	private int mCurrentFrame;
	
	/** Duration in seconds. */
	private int mDurationInSeconds;
	
	/** Observers of the seek position. They will be notified whenever it changes. */
	private List<ISeekPositionObserver> mSeekPositionObservers = new ArrayList<ISeekPositionObserver>();

	/** Observers of the equalizer values. They will be notified whenever they change. */
	private List<IEqualizerObserver> mEqualizerObservers = new ArrayList<IEqualizerObserver>();
	
	/** The current seek position in seconds. */
	private int mCurrentSeekPosition;
	
	/** The volume of the channel A. */
	private int mVolumeA;
	/** The volume of the channel B. */
	private int mVolumeB;
	/** The volume of the channel C. */
	private int mVolumeC;
	/** The noise value. */
	private int mNoiseValue;
	
	/** The bits of the channels using the noise (bit 0 to 1 = noise on channel A etc.). */
	private int mChannelsUsingNoise;
	
	/** The registers to be played. They evolve according to the music replayFrequency, not at each frame. */
	private int[] mRegs = new int[NB_REGISTERS_FROM_YM4];	// Uses the biggest number of registers.
	
	/**
	 * Constructor of the SongReader. The Song MUST be in the correct format.
	 * The doesRawDataFit method must have been called before to ensure that.
	 * @param data the raw binary data of the song.
	 */
	public YMSongReader(byte[] data) {
		this.mData = data;
		readSongInformation();
		song = new Song(data, mReplayFrequency, mPSGFrequency, mSongName, mAuthor, mComments);
	}

	
	// ---------------------------------------------------------------------
	// Getters and setters
	// ---------------------------------------------------------------------

	/**
	 * Returns the Song read by this Reader.
	 * @return the Song read by this Reader.
	 */
	public Song getSong() {
		return song;
	}

	@Override
	public int getReplayFrequency() {
		return mReplayFrequency;
	}

	@Override
	public int getPSGFrequency() {
		return mPSGFrequency;
	}
	
	@Override
	public String getName() {
		return mSongName;
	}

	@Override
	public String getAuthor() {
		return mAuthor;
	}


	@Override
	public String getComments() {
		return mComments;
	}

	@Override
	public int getDuration() {
		return mDurationInSeconds;
	}

	@Override
	public String getFormat() {
		return "YM " + mYMVersion;
	}


	// ***************************************
	// Private methods
	// ***************************************

	/**
	 * Parses the given raw binary data and fills the field of the song.
	 * The Song MUST be in the correct format. The doesRawDataFit method must have been called before to ensure that.
	 */
	private void readSongInformation() {
		mYMVersion = mData[2] - '0';
		
		// These are default values. They are given only from YM5 and above.
		mPSGFrequency = DEFAULT_PSG_FREQUENCY;
		mReplayFrequency = DEFAULT_REPLAY_FREQUENCY;
		int index = FRAME_LOOP_START_OFFSET;
		
		if (mYMVersion <= 3) {
			int endTagSize;
			// Is it a version 3B ?
			if (mData[3] - '0' == 'b') {
				// If yes, the loop information is encoded at the end.
				endTagSize = YM3B_LOOP_INFORMATION_SIZE;
				mFrameLoopStart = SongUtil.readDWord(mData, mData.length - endTagSize);
			} else {
				endTagSize = 0;
				mFrameLoopStart = 0;
			}
			
			// These versions don't hold any information.
			mNbFrames = (mData.length - HEADER_TAG_SIZE - endTagSize) / NB_REGISTERS_TILL_YM3;		// Removes the header.
			mRegistersBaseOffset = HEADER_TAG_SIZE;
			mIsInterleaved = true;
			mAuthor = "";
			mSongName = "";
			mComments = "";
		} else {
			// YM Version 4 and more.
			
			mNbFrames = SongUtil.readDWord(mData, NB_VALID_VBL_OFFSET);
			int songAttributes = SongUtil.readDWord(mData, SONG_ATTRIBUTES_OFFSET);
			
			mIsInterleaved = ((songAttributes & 1) != 0);
			mAreSamplesSigned = ((songAttributes & 2) != 0);
			mAreSamplesInST4BitsFormat = ((songAttributes & 4) != 0);
			
			mNbSamples = SongUtil.readWord(mData, NB_DIGIDRUMS_OFFSET);
			
			// Information about the frequency of the PSG and the song are only available from YM5.
			if (mYMVersion >=5) {
				mPSGFrequency = SongUtil.readDWord(mData, PSG_FREQUENCY_OFFSET);
				mReplayFrequency =  SongUtil.readWord(mData, REPLAY_FREQUENCY_OFFSET);
				index = FRAME_LOOP_START_OFFSET_YM5;
			}
			
			mFrameLoopStart = SongUtil.readDWord(mData, index);
			index += DWORD_SIZE;
			
			// Skips possible additional data.
			if (mYMVersion >= 5) {
				int additionalDataSize = SongUtil.readWord(mData, index);
				index += WORD_SIZE + additionalDataSize;
			}
	
			// Reads the samples, if any.
			if (mNbSamples > 0) {
				samples = new byte[mNbSamples][];
				
				for (int i = 0 ; i < mNbSamples; i++) {
					int sampleSize = SongUtil.readDWord(mData, index);
					index += DWORD_SIZE;
					samples[i] = new byte[sampleSize];
					
					// Reads the sample data itself.
					for (int j = 0 ; j < sampleSize; j++) {
						// Make them use only 4 bits if they don't already, and unsigned.
						int value = (mData[index++] & 0xff);
						if (!mAreSamplesInST4BitsFormat) {
							value >>>= 4;
						}
						if (mAreSamplesSigned) {
							value += 8;
						}
						samples[i][j] = (byte)value;
					}
					
				}
			}
			
			mSongName = SongUtil.readNTString(mData, index);
			index += mSongName.length() + 1;		// Go next to the field. +1 because there's a 0 ending the string.
			mAuthor = SongUtil.readNTString(mData, index);
			index += mAuthor.length() + 1;
			mComments = SongUtil.readNTString(mData, index);
			mRegistersBaseOffset = index + mComments.length() + 1;
		}
		mCurrentFrame = 0;
		mDurationInSeconds = (mNbFrames / mReplayFrequency);
		mCurrentSeekPosition = calculateSeekPosition();
	}

	/**
	 * Calculates the seek position in seconds according to the current frame and the replay frequency.
	 * @return the seek position in seconds.
	 */
	private int calculateSeekPosition() {
		return mCurrentFrame / mReplayFrequency;
	}


	// ---------------------------------------------------------------------
	// Public methods.
	// ---------------------------------------------------------------------
	
	@Override
	public int[] getNextRegisters() {
		int nbRegisters = (mYMVersion > 3) ? NB_REGISTERS_FROM_YM4 : NB_REGISTERS_TILL_YM3;
		
		if (mIsInterleaved) {
			// V0R0,V1R0,V2R0,....,VnR0
			// V0R1,V1R1,V2R1,....,VnR1
			int index = mRegistersBaseOffset + mCurrentFrame;
			for (int i = 0; i < nbRegisters; i++) {
				int temp = index + i * mNbFrames;
				mRegs[i] = (mData[temp] & 0xff);
			}
		} else {
			// V0R0,V0R1,V0R2,....,V0R14,V0R15
			// V1R0,V1R1,V1R2,....,V1R14,V1R15
			int index = mRegistersBaseOffset + mCurrentFrame * nbRegisters;
			for (int i = 0; i < nbRegisters; i++) {
				mRegs[i] = (mData[index + i] & 0xff);
			}
		}

		// Next frame.
		if (++mCurrentFrame >= mNbFrames) {
			mCurrentFrame = mFrameLoopStart;
		}
		
		int newSeekPosition = calculateSeekPosition();
		setNewSeekPositionAndNotifyIfNeeded(newSeekPosition);
		fillVolumeAndNoiseValues(mRegs);
		
		notifyNewEqualizerValues();
		
		return mRegs;
	}

	@Override
	public byte[] getSample(int sampleNumber) {
		byte[] sample = null;
		
		if ((samples != null) && (sampleNumber < samples.length)) {
			sample = samples[sampleNumber];
		}
		
		return sample;
	}
	

	@Override
	public void addSeekObserver(ISeekPositionObserver observer) {
		mSeekPositionObservers.add(observer);
	}
	
	@Override
	public void addEqualizerObserver(IEqualizerObserver observer) {
		mEqualizerObservers.add(observer);
	}

	@Override
	public void removeSeekObserver(ISeekPositionObserver observer) {
		mSeekPositionObservers.remove(observer);
	}

	@Override
	public void removeEqualizerObserver(IEqualizerObserver observer) {
		mEqualizerObservers.remove(observer);
	}


	@Override
	public void seek(int seconds) {
		mCurrentFrame = calculateFrameNumberFromSeconds(seconds);
		setNewSeekPositionAndNotifyIfNeeded(seconds);
		//currentSeekPosition = seconds;
	}

	@Override
	public int getVolumeChannel(int channel) {
		int value = 0;
		switch (channel) {
		case 1:
			value = mVolumeA;
			break;
		case 2:
			value = mVolumeB;
			break;
		case 3:
			value = mVolumeC;
			break;
		}
		return value;
	}


	@Override
	public int getNoiseValue() {
		return mNoiseValue;
	}


	@Override
	public int getNoiseChannels() {
		return mChannelsUsingNoise;
	}

	
	
	// ---------------------------------------------------------------------
	// Private methods.
	// ---------------------------------------------------------------------

	/**
	 * Fills the volume and noise values so that the equalizer can read them when it wants.
	 * @param regs the PSG registers.
	 */
	private void fillVolumeAndNoiseValues(int[] regs) {
		int value = regs[8];
		if (value > 16) { value = 16; }
		mVolumeA = value;

		value = regs[9];
		if (value > 16) { value = 16; }
		mVolumeB = value;
		
		value = regs[10];
		if (value > 16) { value = 16; }
		mVolumeC = value;
		
		mNoiseValue = regs[6] & BinaryConstants.B_00011111;
		
		// Takes the bits 3,4,5 and inverts them because for the PSG, 0 is "open".
		mChannelsUsingNoise = ((regs[7] >> 3) ^ 111) & BinaryConstants.B_00000111;
	}

	/**
	 * Sets the given seek position and notifies the change, but only if the seek position is different from
	 * the current one.
	 * @param newSeekPosition the new seek position to set.
	 */
	private void setNewSeekPositionAndNotifyIfNeeded(int newSeekPosition) {
		if (mCurrentSeekPosition != newSeekPosition) {
			mCurrentSeekPosition = newSeekPosition;
			notifyNewSeekPosition();
		}
	}
	
	/**
	 * Notifies the seek position observers about a change.
	 */
	private void notifyNewSeekPosition() {
		for (ISeekPositionObserver observer : mSeekPositionObservers) {
			observer.notifyNewSeekPositionFromSubject(mCurrentSeekPosition);
		}
	}

	/**
	 * Calculates the frame number according to the given second, with a maximum of the duration of song tested. 
	 * @param second the second of the song to reach.
	 * @return the frame number.
	 */
	private int calculateFrameNumberFromSeconds(int second) {
		int frameNumber = second * mReplayFrequency;
		return frameNumber > mNbFrames ? mNbFrames : frameNumber;
	}
	
	/**
	 * Notifies the new equalizer values to observers.
	 */
	private void notifyNewEqualizerValues() {
		for (IEqualizerObserver observer : mEqualizerObservers) {
			observer.notifyNewEqualizerValues(mVolumeA, mVolumeB, mVolumeC, mNoiseValue);
		}
	}
	
	
	
	// ---------------------------------------------------------------------
	// Static public methods.
	// ---------------------------------------------------------------------
	
	/**
	 * Indicates if the given raw binary data fits the format of the song. It may be packed in LHA or not.
	 * @param musicFile the file containing the song.
	 * @return the given raw binary data fits the format of the song, or null if something went wrong.
	 */
	public static byte[] doesRawDataFit(File musicFile) {
		
		// First checks if the input stream is a LHA file.
		byte[] data = null;
		
		try {
			data = FileUtils.unpackLHAFile(musicFile);
		} catch (Exception e) {
			Log.e(DEBUG_TAG, e.getMessage());
		}
		
		if (data == null) {
			return null;
		}
		
		// Checks for the YMx! tag. The Leonard tag following is absent for YM2!/YM3!/YM3b, so we don't test it.
		boolean result = (data.length > 4) && (data[0] == 'Y') && (data[1] == 'M') && ((data[3] == '!') || (data[3] == 'b'));
		
		return result ? data : null;
	}








	

}
