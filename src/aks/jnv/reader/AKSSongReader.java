package aks.jnv.reader;

import java.util.ArrayList;
import java.util.List;

import aks.jnv.audio.ISeekPositionObserver;
import aks.jnv.song.Song;

/**
 * Reader of an AKS binary song (Arkos Tracker).
 * 
 * @author Julien Névo
 *
 */
public class AKSSongReader implements ISongReader {

	private static final String UNKNOWN = "Unknown";
	private static final String FORMAT = "Arkos Tracker 1.0";

	/** The song being read.*/
	private Song song;
	
	private short[] data;			// The data of the song.
	private int dataPointer;		// Index to what is read in the data.

	private int replayFrequency;	// The replay frequency of the song, in Hz (50hz, 25hz etc.).
	private int PSGFrequency;		// The frequency of the sound processor for which the song has been made, in Hz.
	
	/** Observers of the seek position. They will be notified whenever it changes. */
	private List<ISeekPositionObserver> seekPositionObservers = new ArrayList<ISeekPositionObserver>();
	
	/**
	 * Constructor.
	 * @param song The AKSSong this reader must read.
	 */
//	public AKSSongReader(AKSSong song) {
//		this.song = song;
//	}
	
	/**
	 * Constructor of the SongReader. The Song MUST be in the correct format.
	 * The doesRawDataFit method must have been called before to ensure that.
	 * @param data the raw binary data of the song.
	 */
	public AKSSongReader(short[] data) {
		readSongInformation(data);
		song = new Song(data, replayFrequency, PSGFrequency, null, null, null);
	}
	
	// ***************************************
	// Getters and setters
	// ***************************************

	/**
	 * Returns the Song read by this Reader.
	 * @return the Song read by this Reader.
	 */
	public Song getSong() {
		return song;
	}

	@Override
	public int getReplayFrequency() {
		return replayFrequency;
	}

	@Override
	public int getPSGFrequency() {
		return PSGFrequency;
	}
	
	@Override
	public short[] getSample(int sampleNumber) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getName() {
		return UNKNOWN;
	}

	@Override
	public String getAuthor() {
		return UNKNOWN;
	}


	@Override
	public String getComments() {
		return UNKNOWN;
	}

	@Override
	public int getDuration() {
		// TODO GetDuration
		return 666;
	}

	@Override
	public String getFormat() {
		return FORMAT;
	}
	
	// ***************************************
	// Private methods
	// ***************************************

	/**
	 * Parses the given raw binary data and fills the field of the song.
	 * The Song MUST be in the correct format. The doesRawDataFit method must have been called before to ensure that.
	 * @param data the raw binary data of the song.
	 */
	private void readSongInformation(short[] data) {
		// TODO ***********************
	}
	

	// ***************************************
	// Public methods
	// ***************************************

	
	@Override
	public short[] getNextRegisters() {
		return null;
	}

	/**
	 * Indicates if the given raw binary data fits the format of the song.
	 * @param data the raw binary data of the song.
	 * @return true if the given raw binary data fits the format of the song.
	 */
	public static boolean doesRawDataFit(short[] data) {
		// FIXME
		return false;
	}

	@Override
	public void addSeekObserver(ISeekPositionObserver observer) {
		seekPositionObservers.add(observer);
	}

	@Override
	public void seek(int seconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getVolumeChannel(int channel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNoiseValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNoiseChannels() {
		// TODO Auto-generated method stub
		return 0;
	}




}
