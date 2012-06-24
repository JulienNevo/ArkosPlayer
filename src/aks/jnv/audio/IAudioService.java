package aks.jnv.audio;

import java.io.File;

import aks.jnv.view.IMusicView;

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
