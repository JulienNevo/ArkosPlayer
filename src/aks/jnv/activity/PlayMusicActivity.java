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

package aks.jnv.activity;

import java.io.File;

import aks.jnv.R;
import aks.jnv.accelerometer.AccelerometerManager;
import aks.jnv.accelerometer.IAccelerometerListener;
import aks.jnv.audio.AudioService;
import aks.jnv.util.PreferenceUtils;
import aks.jnv.view3d.EqualizerGLSurfaceView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Activity used to see the music being played, information about it, command button, and a seek bar.
 * 
 * TODO :
 * - One of the song doesn't play digidrums?
 * 
 * - Finish the preferences.
 * - Pause acts as a stop.
 * - When leaving (back) the player, and going back to it thanks to the notification icon,
 * 	 the UI is empty.
 * 
 * - Handle unknown format song (next song if possible ?).
 * - Cybern2 doesn't crash, but strange looping ending. Cybern3 (YM6) is good though.
 * - Service stuttering : Maybe a "process" in the xml would be enough ?
 * 						  http://www.vogella.de/articles/AndroidServices/article.html
 * 						  <!-- android:process=":my_process" -->
 * 						  --> No, probably just need to create a worker Thread and that's all.
 * - If the last frame of an YM isn't correctly encoded, prevent the crash (Molusk song).
 * - Show digidrums in the EQ.
 * 
 * - Make a 2D equalizer.
 * - Make a fade out option (after the song has looped).
 * 
 * @author Julien Névo
 * 
 */
public class PlayMusicActivity extends BaseActivity implements IAccelerometerListener {

	/** The debug tag of this class. */
	//private static final String LOG_TAG = PlayMusicActivity.class.getSimpleName();
	
	/** Helper class to broadcast only within the application. */
	private static LocalBroadcastManager mLocalBroadcastManager;
	
	/** String written when the song information is not available. */
	//private static final String UNKNOWN = "Unknown";
	//private static final String UNKNOWN_DURATION = "0:00";
	
	//private IMusicController musicController = MusicController.getInstance();
	
	/** The tag used to give this Activity the name of the song to play. */
	public static final String EXTRA_SONG_PATH = "songPath";

	/** Stores the song duration in seconds. */
	private int mSongDurationInSeconds;
	
	/** The TextView where the song information are displayed. */
	private TextView mMainTextView;
	
	/** The TextView that shows the current position in the song in seconds. */
	private TextView mCurrentPositionInSecondsTextView;
	/** The TextView that shows the remaining duration in seconds. */
	private TextView mRemainingDurationInSecondsTextView;
	/** The SeekBar in order to see the position in the music, and to allow the user to choose where to go. */
	private SeekBar mSeekBar;
	
	/** The Play button. It may be invisible. */
	private Button mPlayButton;
	/** The Next Song button. */
	private Button mNextButton;
	/** The Previous Song button. */
	private Button mPreviousButton;
	/** The Pause button. It may be invisible. */
	private Button mPauseButton;
	/** The Selection Music button. */
	private Button mSelectMusic;
	/** The possible song being played. */
	private File mSong;

	/** BroadcastReceiver to get Intents from the Audio Service. */
	private BroadcastReceiver mAudioBroadcastReceiver;

	/** Indicates whether the user is being sliding the slider. */
	private boolean mIsUserSliding;
	
	/** The Equalizer GLView. */
	private EqualizerGLSurfaceView mEqualizerGLView;
	
	/** Indicates if the Equalizer is used and visible. */
	private boolean mIsEqualizerVisible;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Log.e(LOG_TAG, "onCreate");
		
		// Removed the title bar.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_play_music);
		
		// Gets the possible song to play from the calling Activity.
		setNewSongFromIntent();
		
		
		// Retrieves the objects of the layout.
		mMainTextView = (TextView)findViewById(R.id.play_music_maintext_textview);
		mRemainingDurationInSecondsTextView = (TextView)findViewById(R.id.play_music_duration_textview);
		mCurrentPositionInSecondsTextView = (TextView)findViewById(R.id.play_music_current_position_textview);
		
		mPlayButton = (Button)findViewById(R.id.play_music_play_button);
		mPauseButton = (Button)findViewById(R.id.play_music_pause_button);
		
		mNextButton = (Button)findViewById(R.id.play_music_next_button);
		mPreviousButton = (Button)findViewById(R.id.play_music_previous_button);
		
		mSelectMusic = (Button)findViewById(R.id.play_music_music_selection_button);
		
		mSeekBar = (SeekBar)findViewById(R.id.play_music_seekbar);
		
		mEqualizerGLView = (EqualizerGLSurfaceView)findViewById(R.id.play_music_equalizer3dview);
		
		setFieldsToUnkown();
		
//		updateSongInformationFomService();
		
		//showPlayOrPauseButton(true);
		
		// Sets up the interactions of the widgets of the layout.
		mPlayButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playOrPause();
			}
		});
		
		//stopButton.setOnClickListener(new OnClickListener() {
		mPauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// If no song is being played, directly go to the MusicSelection Activity.
				if (mSong == null) {
					goToMusicSelectionActivity();
				}
				
				showPlayOrPauseButton(true);
				
				Intent intent = new Intent(PlayMusicActivity.this, AudioService.class);
				intent.setAction(AudioService.STOP_SONG_RECEIVED_ACTION);
				startService(intent);
				
//				if (isBound) {
//					audioService.stop();
//				}
			}
		});
		
		mNextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		mPreviousButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mIsUserSliding = false;
				
				// Asks the Service to seek a new position.
				int seekPosition = seekBar.getProgress();
				
				Intent intent = new Intent(PlayMusicActivity.this, AudioService.class);
				intent.setAction(AudioService.SEEK_POSITION_RECEIVED_ACTION);
				intent.putExtra(AudioService.ACTION_EXTRA_NEW_SEEK_VALUE, seekPosition);
				startService(intent);
				
				//updatePositionTextViews(seekPosition, songDurationInSeconds);
				//audioService.seek(seekPosition);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mIsUserSliding = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					// Updates the duration only.
					int seekPosition = seekBar.getProgress();
					updatePositionTextViews(seekPosition, mSongDurationInSeconds);
				}
			}
		});
		
		mSelectMusic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Opens the Music Selection Activity.
				goToMusicSelectionActivity();
			}
		});
		
		// Plays the song.
		//mPlayButton.performClick();
		//playOrPause();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// This is needed because this Activity is declared as SingleTop, so when one of the MusicSelectionActivity starts it again with a new music,
		// onCreate is not called, and onResume is called but the Intent is the old one. This method allows the new Intent to be used instead of the
		// old one.
		setIntent(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Log.e(LOG_TAG, "onResume");
		setNewSongFromIntent();
		
		// Is the equalizer visible?
		mIsEqualizerVisible = PreferenceUtils.isEqualizerUsed(this);
		mEqualizerGLView.setVisibility(mIsEqualizerVisible ? View.VISIBLE : View.GONE);
		
		
		String songPath = getIntent().getStringExtra(EXTRA_SONG_PATH);
		//Log.e(LOG_TAG, "Song played = " + songPath);
		
		if (mIsEqualizerVisible) {
	        // Listens to the Accelerometer, only if the equalizer is visible, as only it uses it.
			AccelerometerManager.setContext(this);
			if (AccelerometerManager.isAccelerometerPresent()) {
				AccelerometerManager.startListening(this);
			}
		}
		
		// Registers to the Broadcast Receiver from the AudioService.
		if (mLocalBroadcastManager == null) {
			mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		}
		// Tells the filter all the actions we'll react about.
		IntentFilter filter = new IntentFilter();
		filter.addAction(AudioService.ACTION_UPDATE_SONG_INFORMATION_FROM_SERVICE);
		filter.addAction(AudioService.ACTION_UPDATE_SONG_SEEK_FROM_SERVICE);
		// Listens to the Equalizer values only if needed.
		if (mIsEqualizerVisible) {
			filter.addAction(AudioService.ACTION_UPDATE_EQUALIZER_VALUES);
		}
		
		// Creates the BroadcastReceiver. It will react to the intents from the Audio Service.
		mAudioBroadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// We have received a message. What action?
				if (action.equals(AudioService.ACTION_UPDATE_SONG_INFORMATION_FROM_SERVICE)) {
					// Updates the song information, on the UI Thread.
					final String musicName = intent.getStringExtra(AudioService.ACTION_EXTRA_SONG_NAME);
					final String author = intent.getStringExtra(AudioService.ACTION_EXTRA_SONG_AUTHOR);
					final String comments = intent.getStringExtra(AudioService.ACTION_EXTRA_SONG_COMMENTS);
					final String format = intent.getStringExtra(AudioService.ACTION_EXTRA_SONG_FORMAT);
					final int duration = intent.getIntExtra(AudioService.ACTION_EXTRA_SONG_DURATION, 0);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateSongInformation(musicName, author, comments, format, duration);
						}
					}); 
				} else if (action.equals(AudioService.ACTION_UPDATE_SONG_SEEK_FROM_SERVICE)) {
					// The service has notified of a new seek position. Updates it on the UI Thread.
					final int newSeek = intent.getIntExtra(AudioService.ACTION_EXTRA_NEW_SEEK_VALUE, 0);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateSongSeekPosition(newSeek);
						}
					});
				} else if (action.equals(AudioService.ACTION_UPDATE_EQUALIZER_VALUES)) {
					// The service has notified of new Equalizer values.
					int volumeChannel1 = intent.getIntExtra(AudioService.ACTION_EXTRA_VOLUME_CHANNEL1, 0);
					int volumeChannel2 = intent.getIntExtra(AudioService.ACTION_EXTRA_VOLUME_CHANNEL2, 0);
					int volumeChannel3 = intent.getIntExtra(AudioService.ACTION_EXTRA_VOLUME_CHANNEL3, 0);
					int noise = intent.getIntExtra(AudioService.ACTION_EXTRA_NOISE, 0);
					setEqualizerValues(volumeChannel1, volumeChannel2, volumeChannel3, noise);
				}
			}
		};
		
		
		mLocalBroadcastManager.registerReceiver(mAudioBroadcastReceiver, filter);
		
		// Plays the song.
		playOrPause();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	
		// Stops listening to the Accelerometer.
		if (AccelerometerManager.isSensorActive()) {
			AccelerometerManager.stopListening();
		}
		
		// Unregisters to the Broadcast Receiver from the AudioService.
		if ((mLocalBroadcastManager != null) && (mAudioBroadcastReceiver != null)) {
			mLocalBroadcastManager.unregisterReceiver(mAudioBroadcastReceiver);
		}
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//doUnbindService();
	}

	
	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		mEqualizerGLView.onAccelerationChanged(x, y, z);
	}

	
	// --------------------------------------------------------------------
	// Private methods.
	// --------------------------------------------------------------------
	
	/**
	 * Gets the song path and name from the intent.
	 */
	private void setNewSongFromIntent() {
		String songPath = getIntent().getStringExtra(EXTRA_SONG_PATH);
		if (songPath != null) {
			File musicFile = new File(songPath);
			if (musicFile != null) {
				mSong = musicFile;
			}
		}
	}
	
	/**
	 * The Play/Pause button has been clicked.
	 */
	private void playOrPause() {
		showPlayOrPauseButton(false);
		
		//Log.e(LOG_TAG, "Is mSong null? " + (mSong == null));
				
		if (mSong != null) {
			//Log.e(LOG_TAG, "Song played = " + mSong.getName());
			Intent intent = new Intent(PlayMusicActivity.this, AudioService.class);
			intent.setAction(AudioService.PLAY_SONG_RECEIVED_ACTION);
			intent.putExtra(AudioService.EXTRA_SONG_NAME, mSong.getAbsolutePath());
			startService(intent);
		}
	}
	
	/**
	 * Shows the Play or Pause button.
	 * @param isPlayButtonShown true to show the Play button, false to show the Pause button.
	 */
	private void showPlayOrPauseButton(boolean isPlayButtonShown) {
		mPlayButton.setVisibility(isPlayButtonShown ? View.VISIBLE : View.GONE);
		mPauseButton.setVisibility(isPlayButtonShown ? View.GONE : View.VISIBLE);
	}
	
	/**
	 * Updates the song information.
	 * @param musicName The name of the music. May be null.
	 * @param author The author. May be null.
	 * @param comments The comments. May be null.
	 * @param format The format. May be null.
	 * @param duration The duration in seconds. May be 0.
	 */
	private void updateSongInformation(String musicName, String author, String comments, String format, int duration) {
		mSongDurationInSeconds = duration;
		mSeekBar.setMax(duration > 0 ? duration - 1 : 0);
		mSeekBar.setProgress(0);
		updatePositionTextViews(0, duration);
		//mRemainingDurationInSecondsTextView.setText("-" + convertDurationToMinutes(mSongDurationInSeconds));
		
		
		// Builds the main text.
		StringBuffer sb = new StringBuffer();
		
		// Uses a place-holder as a title if none is present. 
		if ((musicName == null) || (musicName.length() == 0)) {
			musicName = getString(R.string.play_music_untitled_song);
		}
		sb.append("<b>").append(musicName).append("</b><br />");
		if ((author != null) && (author.length() > 0)) {
			sb.append(getString(R.string.play_music_by));
			sb.append("<br />");
			sb.append("<b>").append(author).append("</b><br /><br />");
		}
		if ((comments != null) && (comments.length() > 0)) {
			sb.append("\"").append(comments).append("\"<br />");
		}
		if ((format != null) && (format.length() > 0)) {
			sb.append(getString(R.string.play_music_format)).append(format);
		}
		mMainTextView.setText(Html.fromHtml(sb.toString()));
	}
	
	/**
	 * Updates the seek position visually. If the user is already seeking, we don't show anything.
	 * @param seekPosition The seek position, in seconds.
	 */
	private void updateSongSeekPosition(int seekPosition) {
		// Don't update the position if the user is already seeking.
		if (!mIsUserSliding) {
			mSeekBar.setProgress(seekPosition);
			updatePositionTextViews(seekPosition, mSongDurationInSeconds);
		}
	}
	

//	@Override
//	protected void updateSongInformationFomService() {
//		// Asks the service to give it the song information, if it exists.
//		SongInformation songInformation = (audioService != null) ? audioService.getSongInformation() : null;
//		if (songInformation != null) {
//			String author = songInformation.getAuthor();
//			String comments = songInformation.getComments();
//			String format = songInformation.getFormat();
////			musicNameTextView.setText(songInformation.getName());
////			authorTextView.setText(author);
////			commentsTextView.setText(comments);
////			formatTextView.setText(format);
//			
//			songDurationInSeconds = songInformation.getSongDurationInSeconds();
//			seekBar.setMax(songDurationInSeconds - 1);
//			seekBar.setProgress(0);
//			remainingDurationInSecondsTextView.setText("-" + convertDurationToMinutes(songDurationInSeconds));
//			
//			// Builds the main text.
//			StringBuffer sb = new StringBuffer();
//			sb.append("<b>").append(songInformation.getName()).append("</b><br />");
//			if (author.length() > 0) {
//				sb.append("by:<br />");
//				sb.append("<b>").append(author).append("</b><br /><br />");
//			}
//			if (comments.length() > 0) {
//				sb.append("\"").append(comments).append("\"<br />");
//			}
//			sb.append("Format: ").append(format);
//			mainTextView.setText(Html.fromHtml(sb.toString()));
//		} else {
//			StringBuffer sb = new StringBuffer();
//			sb.append("No song loaded yet.");
//			mainTextView.setText(Html.fromHtml(sb.toString()));
//
//			setFieldsToUnkown();
//		}
//	}
	
//	@Override
//	protected void updateSongSeekPositionFomService() {
//		// Asks the service to give the current seek position.
//		int seekPosition = audioService.getCurrentSeekPosition();
//		seekBar.setProgress(seekPosition);
//		updatePositionTextViews(seekPosition, songDurationInSeconds);
//	}
	
	

	/**
	 * Sets the fields of the song information to unknown. It can happen at the start, or when a
	 * bad file has been loaded.
	 */
	private void setFieldsToUnkown() {
		//updatePositionTextViews(0, 0);
		//mSeekBar.setMax(0);
		//mSeekBar.setProgress(0);
		
		updateSongInformation(getString(R.string.play_music_no_music_loaded), null, null, null, 0);
	}
	
	/**
	 * Updates the position TextViews according to the given seek position. The song duration must have been set before.
	 * @param seekPosition the Seek position in seconds.
	 * @param songDuration the song duration in seconds.
	 */
	private void updatePositionTextViews(int seekPosition, int songDuration) {
		mCurrentPositionInSecondsTextView.setText(convertDurationToMinutes(seekPosition));
		int remainingDuration = songDuration - seekPosition;
		if (remainingDuration < 0) {
			remainingDuration = 0;
		}
		mRemainingDurationInSecondsTextView.setText("-" + convertDurationToMinutes(remainingDuration));
	}

	/**
	 * Opens the Music Selection Activity.
	 */
	private void goToMusicSelectionActivity() {
		Intent intent = new Intent(PlayMusicActivity.this, MusicSelectionActivity.class);
		startActivity(intent);
	}

	/**
	 * Sets the equalizer values.
	 * @param volumeChannel1 The volume for the channel 1.
	 * @param volumeChannel2 The volume for the channel 2.
	 * @param volumeChannel3 The volume for the channel 3.
	 * @param noise The noise.
	 */
	private void setEqualizerValues(int volumeChannel1, int volumeChannel2, int volumeChannel3, int noise) {
		mEqualizerGLView.setEqualizerValues(volumeChannel1, volumeChannel2, volumeChannel3, noise);		
	}


	// -----------------------------------------------------------------------
	// Static utility methods.
	// -----------------------------------------------------------------------
	
	/**
	 * Converts seconds to minutes.
	 * @param duration the duration
	 * @return the String to be displayed, in minutes and seconds.
	 */
	private static String convertDurationToMinutes(int duration) {
		int seconds = duration % 60;
		int minutes = duration / 60;
		return minutes + ":" + (seconds < 10 ? ("0" + seconds) : seconds);
	}
		

	

}
