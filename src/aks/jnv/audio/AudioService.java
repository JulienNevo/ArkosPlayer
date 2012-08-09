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

import java.io.File;
import aks.jnv.R;
import aks.jnv.reader.ISongReader;
import aks.jnv.song.SongUtil;
import aks.jnv.view.PlayMusicActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class AudioService extends Service implements IAudioService, ISeekPositionObserver {

	/** The debug tag of this class. */
	public static final String DEBUG_TAG = AudioService.class.getSimpleName();
	
	/** Action of playing the song, sent to the Service. */
	public static final String PLAY_SONG_RECEIVED_ACTION = "PLAY_SONG";
	/** Action of stopping the song, sent to the Service. */
	public static final String STOP_SONG_RECEIVED_ACTION = "STOP_SONG";
	/** Action of seeking a position, sent to the Service. */
	public static final String SEEK_POSITION_RECEIVED_ACTION = "SEEK_POSITION";
	
	/** The tag used to give this Activity the name of the song to play. */
	public static final String EXTRA_SONG_NAME = "SONG_NAME";
	
	/** Tag used to identify the Broadcast message from the Service to the Activities when the song information needs an update. */
	public static final String ACTION_UPDATE_SONG_INFORMATION_FROM_SERVICE = "aks.jnv.AudioService.UPDATE_SONG_INFORMATION_FROM_SERVICE";
	/** Tag used to identify the Broadcast message from the Service to the Activities when a new seek value is ready. */
	public static final String ACTION_UPDATE_SONG_SEEK_FROM_SERVICE = "aks.jnv.AudioService.UPDATE_SONG_SEEK_FROM_SERVICE";

	//public final static int TAG_UPDATE_SONG_INFORMATION = 0;
	
	public final static int TAG_UPDATE_SONG_SEEK = 1;
	public final static String MESSAGE_NAME = "MessageFromSong";

	/** Tag for the extra information from the service when it sends an update on the song name. */
	public static final String ACTION_EXTRA_SONG_NAME = "UpdateSongInformationSongName";
	/** Tag for the extra information from the service when it sends an update on the song author. */
	public static final String ACTION_EXTRA_SONG_AUTHOR = "UpdateSongInformationAuthor";
	/** Tag for the extra information from the service when it sends an update on the song comments. */
	public static final String ACTION_EXTRA_SONG_COMMENTS = "UpdateSongInformationComments";
	/** Tag for the extra information from the service when it sends an update on the song format. */
	public static final String ACTION_EXTRA_SONG_FORMAT = "UpdateSongInformationFormat";
	/** Tag for the extra information from the service when it sends an update on the song duration. */
	public static final String ACTION_EXTRA_SONG_DURATION = "UpdateSongInformationDuration";

	/** Tag for the extra information from the service when it sends a new seek value. */
	public static final String ACTION_EXTRA_NEW_SEEK_VALUE = "UpdateSeekValue";

	
	/** The Audio Renderer used to render the song. */
	private AudioRenderer mAudioRenderer;
	
	/** Used to notify the user of what is happening. */
	private NotificationManager mNotificationManager;
	
	/** Unique identification String for the notification. */
	private int NOTIFICATION_ID = R.string.local_service_name;
	
	/** Binder accessed by the client, to have an access to the Service. */
	private final IBinder binder = new LocalBinder();
	
	/** The information of the song (name, author, format...). May be Null. */
	private SongInformation mSongInformation;
	
	/** The current seek position in seconds. */
	private int mCurrentSeekPosition;
	
	/** SongReader given to the AudioRenderer. It is however stored only to get easily the value for the equalizer. */
	private ISongReader mSongReader;

	/** The song to play or being played. */
	private File mSong;

	private IAudioBufferGenerator mAudioBufferGenerator;

	/** Helper class to broadcast only within the application. */
	private static LocalBroadcastManager mLocalBroadcastManager;
	
	/** The song to play or being played full path. This is only to know if the song has changed and thus must be restarted or not. */
	//private String mSongFullPath;
	
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
	@Override
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
		return mSongReader;
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
		//Log.e(DEBUG_TAG, "onCreate");
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		//FileManager.buildMusicFileList(this);
		
		Toast.makeText(this, R.string.started, Toast.LENGTH_SHORT).show();
		
		

		mAudioBufferGenerator = new AYBufferGenerator();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Performed whenever startService is called. We use this to give order to the Service, as this method is called every time (contrary to onCreate which
		// is called only when the Service is actually started).
		
		// What is the Action?
		String action = intent.getAction();
		if (action != null) {
			if (action.equals(PLAY_SONG_RECEIVED_ACTION)) {
				// Plays the song.
				//Log.e(DEBUG_TAG, "PLAY ACTION");
				// TODO Don't restart the song if the song is the same.
				String songPath = intent.getStringExtra(EXTRA_SONG_NAME);
				
				if (songPath != null) {
					File musicFile = new File(songPath);
					if (musicFile != null) {
						mSong = musicFile;
						playSong(mSong);
					}
				}
				
			} else if (action.equals(STOP_SONG_RECEIVED_ACTION)) {
				//Log.e(DEBUG_TAG, "STOP ACTION");
				// Stops the song.
				stopSong();
			} else if (action.equals(SEEK_POSITION_RECEIVED_ACTION)) {
				int newSeekPosition = intent.getIntExtra(ACTION_EXTRA_NEW_SEEK_VALUE, -1);
				//Log.e(DEBUG_TAG, "SEEK ACTION to " + newSeekPosition);
				if (newSeekPosition >= 0) {
					// Performs a seek.
					seek(newSeekPosition);
				}
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		//Log.e(DEBUG_TAG, "onUnbind");
		stopService();
		return false;	// Does not authorize rebinding.
	}
	
	@Override
	public void onDestroy() {
		stopService();
	}
	
	

	public void playSong(File file) {
		// If nothing is to be played, and nothing was previously played, then nothing can be done.
		String currentSongFullPath = mSong == null ? null : mSong.getAbsolutePath();
		
		if ((file == null) && (currentSongFullPath == null)) {
			return;
		}
		
		boolean mustStartNewSong = false;
		
		String newSongFullPath = null;
		if (file == null) {
			mustStartNewSong = true;
		} else {
			// A new File is present. Is it a new song?
			if (currentSongFullPath == null) {
				// There wasn't any old song.
				mustStartNewSong = true;
			} else {
				mustStartNewSong = file.getAbsolutePath() !=  currentSongFullPath;
			}
		}
		
		// FIXME Song still starts from the start. mSongReader must not be recreated? See how position inside the song is managed.
		
		if (mustStartNewSong) {
		
			mSong = file;
	
			mSongReader = SongUtil.getSongReaderFromFile(file);
			
			if (mSongReader == null) {
				Log.e(DEBUG_TAG, "Song format unknown ! " + file.getName());
				// FIXME handle unknown format song.
				mSongInformation = null;
			} else {
				Notification notification = new Notification(R.drawable.ic_launcher, "Playing song.", System.currentTimeMillis());
				
				//Intent notificationIntent = new Intent(this, AudioService.class);
				Intent notificationIntent = new Intent(this, PlayMusicActivity.class);
				
				// Used when the user clicks on the notification.
				//PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				notification.setLatestEventInfo(getApplicationContext(), getText(R.string.notificationTitle), "zopzop", contentIntent);
				
				//notificationManager.notify(NOTIFICATION_ID, notification);
				startForeground(NOTIFICATION_ID, notification);
				
				// Creates a new Audio Renderer if needed.
				if (mAudioRenderer == null) {
					mAudioRenderer = new AudioRenderer();
					mAudioBufferGenerator.setSampleRate(mAudioRenderer.getSampleRate());
					mAudioRenderer.setAudioBufferGenerator(mAudioBufferGenerator);
				}
				
				mAudioRenderer.setSongReader(mSongReader);
				// Registers as a seek position observer.
				mSongReader.addSeekObserver(this);
				
				//audioRenderer.startSound();
				
				mSongInformation = new SongInformation(mSongReader.getName(),
						mSongReader.getAuthor(), mSongReader.getComments(), mSongReader.getFormat(),
						mSongReader.getDuration());
							
				
				Intent intent = new Intent(ACTION_UPDATE_SONG_INFORMATION_FROM_SERVICE);
				intent.putExtra(ACTION_EXTRA_SONG_NAME, mSongReader.getName());
				intent.putExtra(ACTION_EXTRA_SONG_AUTHOR, mSongReader.getAuthor());
				intent.putExtra(ACTION_EXTRA_SONG_COMMENTS, mSongReader.getComments());
				intent.putExtra(ACTION_EXTRA_SONG_FORMAT, mSongReader.getFormat());
				intent.putExtra(ACTION_EXTRA_SONG_DURATION, mSongReader.getDuration());
				sendLocalBroadcast(intent);
			}
		}
		
		// In all case, starts the Audio Renderer.
		mAudioRenderer.startSound();
		
	}



	/**
	 * Stops the song.
	 */
	public void stopSong() {
		mAudioRenderer.stopSound();
		mAudioRenderer = null;
		// Allows the Service to be killed if memory is needed.
		stopForeground(true);
	}

	
	// ***************************************
	// Private methods
	// ***************************************

	/**
	 * Sends a Broadcast through the LocalBroadcastManager, instantiating it if necessary.
	 * @param intent The Intent to broadcast.
	 */
	private void sendLocalBroadcast(Intent intent) {
		if (mLocalBroadcastManager == null) {
			mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		}
		mLocalBroadcastManager.sendBroadcast(intent);
	}
	
	/**
	 * Returns the next song to be played.
	 * @return the File to play, or Null if none could be found.
	 */
//	private File getNextSong() {
//		songNumber++;
//		return FileManager.getMusic(songNumber);
//	}
	
	
	private void stopService() {
		//Log.e(DEBUG_TAG, "stopService");
		mNotificationManager.cancel(NOTIFICATION_ID);
		if (mAudioRenderer != null) {			// Crashes when leaving the Activity if not here. See why (null pointer exception), it is normal?.
			mAudioRenderer.stopSound();
			mAudioRenderer = null;
		}
		
		Toast.makeText(this, R.string.destroyedService, Toast.LENGTH_SHORT).show();
	}
	
	
	
	/**
	 * Shows a notification according to the state of the Service.
	 * @param notificationState the state of the notification.
	 */
//	private void showNotification(NotificationState notificationState) {
//		CharSequence text;
//		// Sets the icon, text and timestamp.
//		switch (notificationState) {
//			case running:
//				text = getText(R.string.running);
//				break;
//			case started:
//				text = getText(R.string.started);
//				break;
//			case stopped:
//				text = getText(R.string.stopped);
//				break;
//			default:
//				text = getText(R.string.unknown);
//				break;
//		}
//
//		Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
//		
//		//Intent notificationIntent = new Intent(this, AudioService.class);
//		Intent notificationIntent = new Intent(this, PlayMusicActivity.class);
//		
//		// Used when the user clicks on the notification.
//		//PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		
//		notification.setLatestEventInfo(getApplicationContext(), getText(R.string.notificationTitle), text, contentIntent);
//		
//		notificationManager.notify(NOTIFICATION_ID, notification);
//	}

	
	
	
	// ------------------------------------
	// IAudioService implementation methods
	// ------------------------------------
	
	@Override
	public void stop() {
		stopSong();
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void play() {
		playSong(mSong);
	}

	@Override
	public void seek(int secondToSeek) {
		mAudioRenderer.seek(secondToSeek);
	}

	@Override
	public boolean setSong(File song) {
		mSong = song;
		return true;
	}


	@Override
	public SongInformation getSongInformation() {
		return mSongInformation;
	}
	
	@Override
	public int getCurrentSeekPosition() {
		return mCurrentSeekPosition;
	}

	@Override
	public int getVolumeChannel(int channel) {
		return (mSongReader != null) ? mSongReader.getVolumeChannel(channel) : 0;
	}

	@Override
	public int getNoiseValue() {
		return (mSongReader != null) ? mSongReader.getNoiseValue() : 0;
	}


	@Override
	public int getNoiseChannels() {
		return (mSongReader != null) ? mSongReader.getNoiseChannels() : 0;
	}
	
	
	// ------------------------------------
	// ISeekPosition implementation methods
	// ------------------------------------


	@Override
	public void notifyNewSeekPositionFromSubject(int seekPosition) {
		// Sends the new seek position, if different from the one previously sent.
		if (mCurrentSeekPosition != seekPosition) {
			mCurrentSeekPosition = seekPosition;
			Intent intent = new Intent(ACTION_UPDATE_SONG_SEEK_FROM_SERVICE);
			intent.putExtra(ACTION_EXTRA_NEW_SEEK_VALUE, seekPosition);
			sendLocalBroadcast(intent);
		}
	}





	
	


	
}
