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

package aks.jnv.view;

import java.io.File;

import aks.jnv.R;
import aks.jnv.accelerometer.AccelerometerManager;
import aks.jnv.accelerometer.IAccelerometerListener;
import aks.jnv.audio.SongInformation;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Activity used to see the music being played, information about it, command button, and a seek bar.
 * 
 * TODO :
 * - Crash if pause then play.
 * - Handle unknown format song (next song if possible ?).
 * - (Digidrums frequency ok??).
 * - Cybern2 doesn't crash, but strange looping ending. Cybern3 (YM6) is good though.
 * - Bionic Commando 1 doesn't work at all (0 second).
 * - Digidrums handled as Short !
 * - Data read handled as Short.
 * - Service is static in FirstActivity... Make AudioService a Singleton !?
 * - Service stuttering : Maybe a "process" in the xml would be enough ?
 * 						  http://www.vogella.de/articles/AndroidServices/article.html
 * 						  <!-- android:process=":my_process" -->
 * 						  --> No, probably just need to create a worker Thread and that's all.
 * - SongInformation and Song shared the same information! Redundancy!
 * - If the last frame of an YM isn't correctly encoded, prevent the crash (Molusk song).
 * - Show digidrums in the EQ.
 * 
 * - Make a fade in/out options (before/after the song has looped).
 * 
 * @author Julien Névo
 * 
 */
public class PlayMusicActivity extends ServiceActivity implements IAccelerometerListener {

	/** String written when the song information is not available. */
	//private static final String UNKNOWN = "Unknown";
	//private static final String UNKNOWN_DURATION = "0:00";
	
	//private IMusicController musicController = MusicController.getInstance();
	
	/** The tag used to give this Activity the name of the song to play. */
	public static final String EXTRA_SONG_NAME = "SONG_NAME";

	/** Stores the song duration in seconds. */
	private int songDurationInSeconds;
	
	/** The TextView where the song information are displayed. */
	private TextView mainTextView;
	
//	private TextView authorTextView;
//	private TextView commentsTextView;
//	private TextView musicNameTextView;
//	private TextView formatTextView;
	/** The TextView that shows the current position in the song in seconds. */
	private TextView currentPositionInSecondsTextView;
	/** The TextView that shows the remaining duration in seconds. */
	private TextView remainingDurationInSecondsTextView;
	/** The SeekBar in order to see the position in the music, and to allow the user to choose where to go. */
	private SeekBar seekBar;
	
	/** The Play button. It may be invisible. */
	private Button playButton;
	/** The Next Song button. */
	private Button nextButton;
	/** The Previous Song button. */
	private Button previousButton;
	/** The Pause button. It may be invisible. */
	private Button pauseButton;
	
	private File mSong;
	
	// FIXME Remove the GLView because it's really CPU consuming!
	//private EqualizerGLSurfaceView glSurfaceView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playmusic);
		
		String songPath = getIntent().getStringExtra(EXTRA_SONG_NAME);
		if (songPath != null) {
			File musicFile = new File(songPath);
			if (musicFile != null) {
				mSong = musicFile;
			}
		}
		
		// FIXME Remove the GLView because it's really CPU consuming!
		//glSurfaceView = (EqualizerGLSurfaceView)findViewById(R.id.playmusic3dview);

		
		
		// Registers the view to the music controller.
		//musicController.addView(this);
		
		// Retrieves the objects of the layout.
		mainTextView = (TextView)findViewById(R.id.maintext);
//		authorTextView = (TextView)findViewById(R.id.authortextplaymusicactivity);
//		commentsTextView = (TextView)findViewById(R.id.commentstextplaymusicactivity);
//		musicNameTextView = (TextView)findViewById(R.id.musicnametextplaymusicactivity);
//		formatTextView = (TextView)findViewById(R.id.formattextplaymusicactivity);
		remainingDurationInSecondsTextView = (TextView)findViewById(R.id.durationtextplaymusicactivity);
		currentPositionInSecondsTextView = (TextView)findViewById(R.id.currentpositiontextplaymusicactivity);
		
		playButton = (Button)findViewById(R.id.playbuttonplaymusicactivity);
		pauseButton = (Button)findViewById(R.id.pausebuttonplaymusicactivity);
		
		nextButton = (Button)findViewById(R.id.nextbuttonplaymusicactivity);
		previousButton = (Button)findViewById(R.id.previousbuttonplaymusicactivity);
		
		seekBar = (SeekBar)findViewById(R.id.seekbarplaymusicactivity);
		
		setFieldsToUnkown();
		
		updateSongInformationFomService();
		
		showPlayOrPauseButton(true);
		
		// Sets up the interactions of the widgets of the layout.
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPlayOrPauseButton(false);
//				if (musicController != null) {
//					musicController.play();
//				}
				Log.e("XXX", "PlayMusicActivity::onClick Start : début");
				if (isBound) {
					Log.e("XXX", "PlayMusicActivity::onClick Start : BOUND");
					audioService.setSong(mSong);
					audioService.play();
					
					// Tells the equalizer where to get its information.
					// FIXME Remove the GLView because it's really CPU consuming!
					//glSurfaceView.setSongReader(audioService.getSongReader());
				}
			}
		});
		
		//stopButton.setOnClickListener(new OnClickListener() {
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPlayOrPauseButton(true);
//				if (musicController != null) {
//					musicController.stop();
//				}
				if (isBound) {
					audioService.stop();
				}
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (musicController != null) {
//					musicController.next();
//				}
			}
		});
		
		previousButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (musicController != null) {
//					musicController.previous();
//				}
			}
		});
		
//		pauseButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				if (musicController != null) {
////					musicController.pause();
////				}
//			}
//		});
		
//		resumeButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				if (musicController != null) {
////					musicController.play();
////				}
//			}
//		});
		
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int seekPosition = seekBar.getProgress();
				updatePositionTextViews(seekPosition, songDurationInSeconds);
				audioService.seek(seekPosition);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
        // Listens to the Accelerometer.
		AccelerometerManager.setContext(this);
		
		if (AccelerometerManager.isAccelerometerPresent()) {
			AccelerometerManager.startListening(this);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	
		// Stops listening to the Accelerometer.
		if (AccelerometerManager.isSensorActive()) {
			AccelerometerManager.stopListening();
		}
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//doUnbindService();
	}

	
	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		//FIXME Remove the GLView because it's really CPU consuming!
		//glSurfaceView.onAccelerationChanged(x, y, z);
	}
	
	/**
	 * Shows the Play or Pause button.
	 * @param isPlayButtonShown true to show the Play button, false to show the Pause button.
	 */
	private void showPlayOrPauseButton(boolean isPlayButtonShown) {
		playButton.setVisibility(isPlayButtonShown ? View.VISIBLE : View.GONE);
		pauseButton.setVisibility(isPlayButtonShown ? View.GONE : View.VISIBLE);
	}

//	@Override
//	public void setMusicController(IMusicController musicController) {
//		this.musicController = musicController;
//	}
//	
//	@Override
//	public void updateSongInformation(String author, String comments, String musicName,
//			String format, String durationInSeconds) {
//		final String auth = author;
//		final String comm = comments;
//		final String musName = musicName;
//		final String frm = format;
//		final String dur = durationInSeconds;
//		
//		handler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				authorTextView.setText(auth);
//				commentsTextView.setText(comm);
//				musicNameTextView.setText(musName);
//				formatTextView.setText(frm);
//				durationInSecondsTextView.setText(dur);
//			}
//		});
//	}
//	
//	@Override
//	public void updateCurrentPosition(int position) {
//		final int pos = position;
//		handler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				currentPositionInSecondsTextView.setText(Integer.toString(pos));
//			}
//		});
//	}
//
//	@Override
//	public void seek(int position) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void notifyPause() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void notifyPlay() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void notifyStop() {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	protected void updateSongInformationFomService() {
		// Asks the service to give it the song information, if it exists.
		SongInformation songInformation = (audioService != null) ? audioService.getSongInformation() : null;
		if (songInformation != null) {
			String author = songInformation.getAuthor();
			String comments = songInformation.getComments();
			String format = songInformation.getFormat();
//			musicNameTextView.setText(songInformation.getName());
//			authorTextView.setText(author);
//			commentsTextView.setText(comments);
//			formatTextView.setText(format);
			
			songDurationInSeconds = songInformation.getSongDurationInSeconds();
			seekBar.setMax(songDurationInSeconds - 1);
			seekBar.setProgress(0);
			remainingDurationInSecondsTextView.setText("-" + convertDurationToMinutes(songDurationInSeconds));
			
			// Builds the main text.
			StringBuffer sb = new StringBuffer();
			sb.append("<b>").append(songInformation.getName()).append("</b><br />");
			if (author.length() > 0) {
				sb.append("by:<br />");
				sb.append("<b>").append(author).append("</b><br /><br />");
			}
			if (comments.length() > 0) {
				sb.append("\"").append(comments).append("\"<br />");
			}
			sb.append("Format: ").append(format);
			mainTextView.setText(Html.fromHtml(sb.toString()));
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("No song loaded yet.");
			mainTextView.setText(Html.fromHtml(sb.toString()));

			setFieldsToUnkown();
		}
	}
	
	@Override
	protected void updateSongSeekPositionFomService() {
		// Asks the service to give the current seek position.
		int seekPosition = audioService.getCurrentSeekPosition();
		seekBar.setProgress(seekPosition);
		updatePositionTextViews(seekPosition, songDurationInSeconds);
	}
	
	

	/**
	 * Sets the fields of the song information to unknown. It can happen at the start, or when a
	 * bad file has been loaded.
	 */
	private void setFieldsToUnkown() {
//		musicNameTextView.setText(UNKNOWN);
//		authorTextView.setText(UNKNOWN);
//		commentsTextView.setText(UNKNOWN);
//		formatTextView.setText(UNKNOWN);
		updatePositionTextViews(0, 0);
		seekBar.setMax(0);
		seekBar.setProgress(0);
	}
	
	/**
	 * Updates the position TextViews according to the given seek position. The song duration must have been set before.
	 * @param seekPosition the Seek position in seconds.
	 * @param songDuration the song duration in seconds.
	 */
	private void updatePositionTextViews(int seekPosition, int songDuration) {
		currentPositionInSecondsTextView.setText(convertDurationToMinutes(seekPosition));
		int remainingDuration = songDuration - seekPosition;
		if (remainingDuration < 0) {
			remainingDuration = 0;
		}
		remainingDurationInSecondsTextView.setText("-" + convertDurationToMinutes(remainingDuration));
	}

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
