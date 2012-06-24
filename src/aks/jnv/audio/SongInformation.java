package aks.jnv.audio;

/**
 * Simple structure containing the information of a song.
 * 
 * @author Julien Névo
 *
 */
public class SongInformation {
	private String name;
	private String author;
	private String comments;
	private String format;
	private int songDurationInSeconds;
	
	public SongInformation(String name, String author, String comments,
			String format, int songDurationInSeconds) {
		this.name = name;
		this.author = author;
		this.comments = comments;
		this.format = format;
		this.songDurationInSeconds = songDurationInSeconds;
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
	 * Returns the format of the song.
	 * @return the format of the song.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Returns the song duration of the song in seconds.
	 * @return the song duration of the song in seconds.
	 */
	public int getSongDurationInSeconds() {
		return songDurationInSeconds;
	}
}
