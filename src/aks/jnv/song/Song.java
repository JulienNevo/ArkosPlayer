package aks.jnv.song;

public class Song {

	private short[] data;			// The data of the song.

	private int replayFrequency;	// The replay frequency of the song, in Hz (50hz, 25hz etc.).
	private int PSGFrequency;		// The frequency of the sound processor for which the song has been made, in Hz.
	private String name;			// The name of the song. May be Null.
	private String author;			// The author of the song. May be Null.
	private String comments;		// The comments of the song. May be Null.
	
	/**
	 * Constructor of a song.
	 * @param data The data of the song.
	 * @param replayFrequency The replay frequency of the song, in Hz (50hz, 25hz etc.).
	 * @param PSGFrequency The frequency of the sound processor for which the song has been made, in Hz.
	 * @param name The name of the song. May be Null.
	 * @param author The author of the song. May be Null.
	 * @param comments The comments of the song. May be Null.
	 */
	public Song(short[] data, int replayFrequency, int PSGFrequency, String name, String author, String comments) {
		this.data = data;
		this.replayFrequency = replayFrequency;
		this.PSGFrequency = PSGFrequency;
		this.name = name;
		this.author = author;
		this.comments = comments;
	}
	
	
	// ***************************************
	// Getters and setters
	// ***************************************
	
	/**
	 * Returns the replay frequency of the song, in Hz (50hz, 25hz etc.).
	 * @return the replay frequency of the song, in Hz.
	 */
	public int getReplayFrequency() {
		return replayFrequency;
	}

	/**
	 * Returns the frequency of the sound processor for which the song has been made, in Hz.
	 * @return the frequency of the sound processor for which the song has been made, in Hz.
	 */
	public int getPSGFrequency() {
		return PSGFrequency;
	}

	/**
	 * Returns the name of the song.
	 * @return the name of the song.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the author of the song.
	 * @return the author of the song.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Returns the comments of the song.
	 * @return the comments of the song.
	 */
	public String getComments() {
		return comments;
	}
	
	/**
	 * Returns the data of the songs.
	 * @return the data of the songs.
	 */
	public short[] getData() {
		return data;
	}

}
