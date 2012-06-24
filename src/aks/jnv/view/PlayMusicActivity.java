package aks.jnv.view;

import aks.jnv.R;
import aks.jnv.accelerometer.AccelerometerManager;
import aks.jnv.accelerometer.IAccelerometerListener;
import aks.jnv.audio.SongInformation;
import aks.jnv.view3d.EqualizerGLSurfaceView;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
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
 * - Wrong frequency, has to hack.
 * - Handle unknown format song (next song if possible ?).
 * - Digidrums handled as Short !
 * - Data read handled as Short.
 * - Service is static in FirstActivity... Make AudioService a Singleton !?
 * - Digidrums frequency has to be calculated.
 * - Digidrums frequency problem (Chamber of Shaolin).
 * - Manage YM1,2,3 (link forgotten...).
 * - Low volume forced with a hack (AYBUfferGenerator, line 507).
 *   because mobile cursor doesn't work anymore ??
 * - Service stuttering : Maybe a "process" in the xml would be enough ?
 * 						  http://www.vogella.de/articles/AndroidServices/article.html
 * 						  <!-- android:process=":my_process" -->
 * 						  --> No, probably just need to create a worker Thread and that's all.
 * 
 * - Make a fade in/out options (before/after the song has looped).
 * 
 * @author Julien Névo
 * 
 */
public class PlayMusicActivity extends ServiceActivity implements IAccelerometerListener {

	/** String written when the song information is not available. */
	private static final String UNKNOWN = "Unknown";
	private static final String UNKNOWN_DURATION = "0:00";
	
	//private IMusicController musicController = MusicController.getInstance();
	
	private TextView mainTextView;
	
//	private TextView authorTextView;
//	private TextView commentsTextView;
//	private TextView musicNameTextView;
//	private TextView formatTextView;
	private TextView currentPositionInSecondsTextView;
	private TextView durationInSecondsTextView;
	private SeekBar seekBar;
	
	private Button playButton;
	private Button nextButton;
	private Button previousButton;
	private Button pauseButton;
	
	private EqualizerGLSurfaceView glSurfaceView;
	
	static private Context theContext;					// TODO HACK FOR DEBUG (no file !)
	static public Context getContext() {
		return theContext;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playmusic);
		
		glSurfaceView = (EqualizerGLSurfaceView)findViewById(R.id.playmusic3dview);
//		MyGLSurfaceRenderer renderer = new MyGLSurfaceRenderer();
//		glSurfaceView.setRenderer(renderer);
		
		
		theContext = this;								// TODO HACK FOR DEBUG (no file !)
		
		// Registers the view to the music controller.
		//musicController.addView(this);
		
		// Retrieves the objects of the layout.
		mainTextView = (TextView)findViewById(R.id.maintext);
//		authorTextView = (TextView)findViewById(R.id.authortextplaymusicactivity);
//		commentsTextView = (TextView)findViewById(R.id.commentstextplaymusicactivity);
//		musicNameTextView = (TextView)findViewById(R.id.musicnametextplaymusicactivity);
//		formatTextView = (TextView)findViewById(R.id.formattextplaymusicactivity);
		durationInSecondsTextView = (TextView)findViewById(R.id.durationtextplaymusicactivity);
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
					audioService.play();
					
					// Tells the equalizer where to get its information.
					glSurfaceView.setSongReader(audioService.getSongReader());
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
				currentPositionInSecondsTextView.setText(convertDurationToMinutes(seekPosition));
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
		glSurfaceView.onAccelerationChanged(x, y, z);
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
		Log.e("XXX", "PlayMusicActivity::updateSongInformationFomService");
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
			
			int duration = songInformation.getSongDurationInSeconds();
			seekBar.setMax(duration - 1);
			seekBar.setProgress(0);
			durationInSecondsTextView.setText(convertDurationToMinutes(duration));
			
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
		//Log.e("XXX", "PlayMusicActivity::updateSongSeekFomService");
		// Asks the service to give the current seek position.
		int seekPosition = audioService.getCurrentSeekPosition();
		seekBar.setProgress(seekPosition);
		currentPositionInSecondsTextView.setText(convertDurationToMinutes(seekPosition));
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
		currentPositionInSecondsTextView.setText(UNKNOWN_DURATION);
		durationInSecondsTextView.setText(UNKNOWN_DURATION);
		seekBar.setMax(0);
		seekBar.setProgress(0);
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
