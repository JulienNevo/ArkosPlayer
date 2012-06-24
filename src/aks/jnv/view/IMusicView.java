package aks.jnv.view;

/**
 * Interface for Views of music.
 * 
 * @author Julien Névo
 *
 */
public interface IMusicView {

	/**
	 * Updates the song information on the GUI.
	 * @param author the author of the song.
	 * @param comments the comments of the song.
	 * @param musicName the name of the song.
	 * @param format the format of the song.
	 * @param durationInSeconds the duration of the song, in seconds.
	 */
	void updateSongInformation(String author, String comments, String musicName,
			String format, String durationInSeconds);

	/**
	 * Updates the current position in the song, in seconds.
	 * @param position the current position in the songs, in seconds.
	 */
	void updateCurrentPosition(int position);
	
	/**
	 * The user has made a seek. Accuracy may not be satisfied, according to the song format.
	 * @param position the position in the songs to reach, in seconds.
	 */
	void seek(int position);
	
	/**
	 * Notification from the controller that Pause has been activated.
	 */
	void notifyPause();

	/**
	 * Notification from the controller that Play has been activated.
	 */
	void notifyPlay();

	/**
	 * Notification from the controller that Stop has been activated.
	 */
	void notifyStop();
}
