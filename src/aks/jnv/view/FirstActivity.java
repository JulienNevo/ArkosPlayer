package aks.jnv.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import aks.jnv.R;
import aks.jnv.accelerometer.AccelerometerManager;
import aks.jnv.accelerometer.IAccelerometerListener;
import aks.jnv.audio.AudioService;
import aks.jnv.audio.AudioServiceContainer;
import aks.jnv.file.FileManager;
import aks.jnv.reader.ISongReader;
import aks.jnv.song.SongUtil;
import aks.jnv.util.Util;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * THIS VIEW IS OBSOLETE !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * ONLY USED TO RUN THE PLAYMusicActivity. We keep it as a test (getContext...).
 * 
 * @author Julien Névo
 */
public class FirstActivity extends Activity {
    
//	static private AudioService audioService;
//	static private boolean isBound = false;
//	
//	static public AudioService getAudioService() {
//		return audioService;
//	}
	
	//private ISongReader songReader;					// Here for now...
	
	
	static private Context theContext;					// TODO HACK FOR DEBUG (no file !)
	static public Context getContext() {
		return theContext;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        theContext = this;								// TODO HACK FOR DEBUG (no file !)
        
        Button startServiceButton = (Button)findViewById(R.id.startServiceButton);
        Button stopServiceButton = (Button)findViewById(R.id.stopServiceButton);
        Button startButton = (Button)findViewById(R.id.startButton);
        Button stopButton = (Button)findViewById(R.id.stopButton);
//        Button nextButton = (Button)findViewById(R.id.nextButton);
        
        
//        File musicFolder = null;
//        if (FileManager.isExternalStorageAvailableForReading()) {
//        	//Log.e("XXX", "aaaa");
//        	musicFolder = FileManager.getMusicFolder(this);
//        }
//        
        
        
        
        //Log.e("XXX", musicFolder.getAbsolutePath());
        //Log.e("XXX", "bbbb");
        
        
        //short[] data = null;
        
        // Opens the song to be read.
//		Resources r = getResources();
//		
//		//InputStream is = r.openRawResource(R.raw.jetsetwilly);
//		//InputStream is = r.openRawResource(R.raw.inertie);
//		//InputStream is = r.openRawResource(R.raw.lastv8);
//		//InputStream is = r.openRawResource(R.raw.molusk);
////		InputStream is = r.openRawResource(R.raw.syntaxterror);		// Packed.
//		InputStream is = r.openRawResource(R.raw.syntaxdecomp);
//		
//		songReader = SongUtil.getSongReaderFromInputStream(is);
		
//		try {
//			byte[] dataByte = Util.readInputStream(is);
//			data = Util.byteArrayToShortArray(dataByte);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// Prepares the Song.
		//songReader = SongUtil.getSongReaderFromRawData(data);
		//audioService.loadSongReader(songReader);			// Can't do it now, audioService isn't prepared at that moment !
		
        startServiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				doBindService();
			}
		});

        stopServiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				doUnbindService();
			}
		});
        
        startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Bundle bundle = new Bundle();
				
				Intent intent = new Intent(FirstActivity.this, PlayMusicActivity.class);
				//intent.putExtras(bundle);
				startActivity(intent);
			}
		});

        stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//audioService.stopSong();
				//AudioServiceContainer.getAudioService().stopSong();
			}
		});
        
//        nextButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});

    }
	

	
	
	
	
	
	
//	private void doBindService() {
//		AudioServiceContainer.setUp(this);
//		AudioServiceContainer.bindService();
//	}
//	
//	private void doUnbindService() {
//		AudioServiceContainer.unbindService();
//	}

	
//	private ServiceConnection serviceConnection = new ServiceConnection() {
//		
//		/**
//		 * Called when the service has been connected. 
//		 */
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			// As we know exactly what service is used, we can cast it directly.
//			audioService = ((AudioService.LocalBinder)service).getService();
//			
//			//audioService.setSongReader(songReader);
//			audioService.startService(new Intent(FirstActivity.this, AudioService.class));
//			
//			// Little notification, just for fun.
//			//Toast.makeText(FirstActivity.this, getText(R.string.notificationTitle), Toast.LENGTH_SHORT);
//		}
//		
//		/**
//		 * Called when the service has been unexpectedly disconnected. This should never happen.
//		 */
//		public void onServiceDisconnected(ComponentName name) {
//			audioService.stopService(new Intent(FirstActivity.this, AudioService.class));
//			audioService = null;
//		}
//		
//	};
//	
//	/**
//	 * Establishes a connection with the explicit service.
//	 */
//	private void doBindService() {
//		if (!isBound) {
//			isBound = bindService(new Intent(this, AudioService.class), serviceConnection, Context.BIND_AUTO_CREATE);
//			if (!isBound) {
//				Log.e("XXX", "Service NON lancé !");
//			}
//		}
//	}
//	
//	/**
//	 * Detaches our connection with the service.
//	 */
//	private void doUnbindService() {
//		if (isBound) {
//			Log.e("XXX", "FirstActivity.doUnBindService");
//			unbindService(serviceConnection);
//			isBound = false;
//		}
//	}
}