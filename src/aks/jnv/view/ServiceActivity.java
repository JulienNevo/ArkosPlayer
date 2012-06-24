package aks.jnv.view;

import aks.jnv.audio.AudioService;
import aks.jnv.audio.AudioService.LocalBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Abstract Activity class that allows the Activity to be registered to the Audio Service.
 *  
 * @author Julien Névo
 *
 */
public abstract class ServiceActivity extends Activity {

	protected AudioService audioService;
	protected boolean isBound;
	protected ServiceBroadcastReceiver broadcastReceiver = new ServiceBroadcastReceiver();
//	protected static IntentFilter filterUpdateSongInformation = new IntentFilter(AudioService.UPDATE_SONG_INFORMATION_FROM_SERVICE);
//	protected static IntentFilter filterUpdateSongSeek = new IntentFilter(AudioService.UPDATE_SONG_SEEK_FROM_SERVICE);
	protected static IntentFilter filterUpdateSongInformation = new IntentFilter(AudioService.UPDATE_SONG_INFORMATION_FROM_SERVICE);
	

	
	/**
	 * This method will be called by the BroadcastReceiver to notify that the song information needs to be
	 * updated.
	 */
    protected abstract void updateSongInformationFomService(); 
    
    /**
     * This method will be called by the BroadcastReceiver to notify that the song seek position needs to be
	 * updated.
     */
    protected abstract void updateSongSeekPositionFomService();
    
    
	/** Connection to the service. */
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.e("XXX", "MYSERVICECONNECTION, onServiceConnected");
			// We've bound to AudioService, cast the IBinder and get AudioService instance
			LocalBinder binder = (LocalBinder) service;
			audioService = binder.getService();
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};
	
	@Override
    protected void onStart() {
        super.onStart();
        // Binds to the service
        bindService(new Intent(this, AudioService.class), connection, Context.BIND_AUTO_CREATE);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//broadcastReceiver = new ServiceBroadcastReceiver();
		registerReceiver(broadcastReceiver, filterUpdateSongInformation);
		//registerReceiver(broadcastReceiver, filterUpdateSongSeek);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}

    @Override
    protected void onStop() {
        super.onStop();
        // Unbinds from the service
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
    
    public class ServiceBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			switch (intent.getIntExtra(AudioService.MESSAGE_NAME, -1)) {
			case AudioService.TAG_UPDATE_SONG_INFORMATION:
				updateSongInformationFomService();
				break;
			case AudioService.TAG_UPDATE_SONG_SEEK:
				updateSongSeekPositionFomService();
				break;
			default:
				Log.e("XXX", "ServiceBroadcastReceiver : unknown message !!");
			}
		}
    	
    }
}
