package aks.jnv.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aks.jnv.R;
import aks.jnv.file.FileManager;
import aks.jnv.reader.ISongReader;
import aks.jnv.song.SongUtil;
import aks.jnv.view.FirstActivity;
import aks.jnv.view.IMusicView;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AudioService extends Service implements IAudioService, ISeekPositionObserver {

	// Tags used to identify the Broadcast message from the Service to the Activities.
	public static final String UPDATE_SONG_INFORMATION_FROM_SERVICE = "aks.jnv.AudioService.UPDATE_SONG_INFORMATION_FROM_SERVICE";
	//public static final String UPDATE_SONG_SEEK_FROM_SERVICE = "aks.jnv.AudioService.UPDATE_SONG_SEEK_FROM_SERVICE";

	public final static int TAG_UPDATE_SONG_INFORMATION = 0;
	public final static int TAG_UPDATE_SONG_SEEK = 1;
	public final static String MESSAGE_NAME = "MessageFromSong";
	
	/** The Audio Renderer used to render the song. */
	private AudioRenderer audioRenderer;
	
	/** Used to notify the user of what is happening. */
	private NotificationManager notificationManager;
	
	/** Unique identification String for the notification. */
	private int NOTIFICATION_ID = R.string.local_service_name;
	
	/** Binder accessed by the client, to have an access to the Service. */
	private final IBinder binder = new LocalBinder();
	
	/** The information of the song (name, author, format...). May be Null. */
	private SongInformation songInformation;
	
	/** The current seek position in seconds. */
	private int currentSeekPosition;
	
	/** SongReader given to the AudioRenderer. It is however stored only to get easily the value for the equalizer. */
	private ISongReader songReader;
	
	/** Number of the song currently played. -1 means that we never played one. */
	//private int songNumber = -1;

	/** Count of songs. */
	//private int songCount = 0;

	
	/**
	 * Private constructor to avoid multiple instances.
	 */
//	private AudioService() {
//	}
	
	
	/** Instance of the AudioService as a Singleton. */
//	private static AudioService instance = null;
	
	/**
	 * Returns the unique instance of the AudioService.
	 * @return the unique instance of the AudioService.
	 */
//	public static synchronized AudioService getInstance() {
//		if (instance == null) {
//			instance = new AudioService();
//		}
//		return instance;
//	}
	
	/**
	 * Prevents the Clone implementations.
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	
	// ***************************************
	// Getters and Setters
	// ***************************************
	
	/**
	 * Sets the Song Reader to the Audio Renderer, allowing it to play a song. Also registers it in order to be
	 * notified whenever the seek position changes.
	 */
//	public void setSongReader(ISongReader songReader) {
//		//this.songReader = songReader;
//		if (audioRenderer != null) {
//			audioRenderer.setSongReader(songReader);
//		}
//		
//		// Registers as a seek position observer.
//		songReader.addSeekObserver(this);
//	}

	/**
	 * Returns the SongReader used, or null.
	 * @return the SongReader used, or null.
	 */
	public ISongReader getSongReader() {
		return songReader;
	}

	
	// ***************************************
	// Public methods
	// ***************************************
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	/**
	 * Allows the user to get access to the Service. 
	 * @author Julien Névo
	 */
	public class LocalBinder extends Binder {
		public AudioService getService() {
			return AudioService.this;
		}
	}
	
	@Override
	public void onCreate() {
		Log.e("XXX", "AudioService.onCreate");
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		//songCount = FileManager.buildMusicFileList(this);
		FileManager.buildMusicFileList(this);
		
		Toast.makeText(this, R.string.started, Toast.LENGTH_SHORT).show();
		
		//Notification notification = new Notification(R.drawable.statusbaricon, "youpla", System.currentTimeMillis());
		
		//startForeground(NOTIFICATION_ID, notification);
		//showNotification(NotificationState.started);
		
		audioRenderer = new AudioRenderer();

		// Creates the AY generator and gives it to the renderer, which is not called anymore.
		IAudioBufferGenerator audioBufferGenerator = new AYBufferGenerator();
		audioBufferGenerator.setSampleRate(audioRenderer.getSampleRate());
		
		audioRenderer.setAudioBufferGenerator(audioBufferGenerator);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.e("XXX", "AudioService.onUnbind");
		stopService();
		return false;	// Does not authorize rebinding.
	}
	
	@Override
	public void onDestroy() {
		stopService();
	}
	
	

	public void playSong(File file) {
		songReader = null;
		
		songReader = SongUtil.getSongReaderFromFile(file);
		
		if (songReader == null) {
			Log.e("XXX", "Song format unknown ! " + file.getName());
			// FIXME handle unknown format song.
			songInformation = null;
		} else {
			Notification notification = new Notification(R.drawable.statusbaricon, "youpla", System.currentTimeMillis());
			
			//Intent notificationIntent = new Intent(this, AudioService.class);
			Intent notificationIntent = new Intent(this, FirstActivity.class);
			
			// Used when the user clicks on the notification.
			//PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			notification.setLatestEventInfo(getApplicationContext(), getText(R.string.notificationTitle), "zopzop", contentIntent);
			
			//notificationManager.notify(NOTIFICATION_ID, notification);
			startForeground(NOTIFICATION_ID, notification);
			
			audioRenderer.setSongReader(songReader);
			// Registers as a seek position observer.
			songReader.addSeekObserver(this);
			
			audioRenderer.startSound();
			
			songInformation = new SongInformation(songReader.getName(),
					songReader.getAuthor(), songReader.getComments(), songReader.getFormat(),
					songReader.getDuration());
						
			
			Intent intent = new Intent(UPDATE_SONG_INFORMATION_FROM_SERVICE);
			intent.putExtra(MESSAGE_NAME, TAG_UPDATE_SONG_INFORMATION);
			sendBroadcast(intent);
		}
		
	}

	public void stopSong() {
		audioRenderer.stopSound();
		
		stopForeground(false);
	}
	
	// ***************************************
	// Private methods
	// ***************************************

	/**
	 * Returns the next song to be played.
	 * @return the File to play, or Null if none could be found.
	 */
//	private File getNextSong() {
//		songNumber++;
//		return FileManager.getMusic(songNumber);
//	}
	
	
	private void stopService() {
		Log.e("XXX", "AudioService.stopService");
		notificationManager.cancel(NOTIFICATION_ID);
		if (audioRenderer != null) {			// Crashes when leaving the Activity if not here. See why (null pointer exception), it is normal?.
			audioRenderer.stopSound();
			audioRenderer = null;
		}
		
		Toast.makeText(this, R.string.destroyedService, Toast.LENGTH_SHORT).show();
	}
	
	
	
	/**
	 * Shows a notification according to the state of the Service.
	 * @param notificationState the state of the notification.
	 */
	private void showNotification(NotificationState notificationState) {
		CharSequence text;
		// Sets the icon, text and timestamp.
		switch (notificationState) {
			case running:
				text = getText(R.string.running);
				break;
			case started:
				text = getText(R.string.started);
				break;
			case stopped:
				text = getText(R.string.stopped);
				break;
			default:
				text = getText(R.string.unknown);
				break;
		}

		Notification notification = new Notification(R.drawable.statusbaricon, text, System.currentTimeMillis());
		
		//Intent notificationIntent = new Intent(this, AudioService.class);
		Intent notificationIntent = new Intent(this, FirstActivity.class);
		
		// Used when the user clicks on the notification.
		//PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.setLatestEventInfo(getApplicationContext(), getText(R.string.notificationTitle), text, contentIntent);
		
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	
	
	
	
	
	
	@Override
	public void stop() {
		stopSong();
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void play() {
		playSong(null);
		
	}

	@Override
	public void seek(int secondToSeek) {
		audioRenderer.seek(secondToSeek);
		
	}

	@Override
	public boolean setSong(File song) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public SongInformation getSongInformation() {
		return songInformation;
	}
	
	@Override
	public int getCurrentSeekPosition() {
		return currentSeekPosition;
	}


	@Override
	public void notifyNewSeekPositionFromSubject(int seekPosition) {
		if (currentSeekPosition != seekPosition) {
			currentSeekPosition = seekPosition;
			Intent intent = new Intent(UPDATE_SONG_INFORMATION_FROM_SERVICE);
			intent.putExtra(MESSAGE_NAME, TAG_UPDATE_SONG_SEEK);
			sendBroadcast(intent);
		}
	}

	@Override
	public int getVolumeChannel(int channel) {
		return (songReader != null) ? songReader.getVolumeChannel(channel) : 0;
	}


	@Override
	public int getNoiseValue() {
		return (songReader != null) ? songReader.getNoiseValue() : 0;
	}


	@Override
	public int getNoiseChannels() {
		return (songReader != null) ? songReader.getNoiseChannels() : 0;
	}




	
	


	
}
