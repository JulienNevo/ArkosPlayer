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
