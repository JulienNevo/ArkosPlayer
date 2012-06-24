package aks.jnv.reader;

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
	short[] getNextRegisters();
	
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
	short[] getSample(int sampleNumber);
	
	
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
