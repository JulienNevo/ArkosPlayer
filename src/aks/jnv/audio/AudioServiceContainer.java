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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Class that creates and possesses the Audio Service we used within the various activities.
 * 
 * FIXME : TO REMOVE.
 * 
 * @author Julien Névo
 *
 */
public class AudioServiceContainer {

//	private static AudioService audioService;
//	private static boolean isBound = false;
//	
//	private static Context context;
//	private static ServiceConnection serviceConnection;
//	
//	public static AudioService getAudioService() {
//		return audioService;
//	}
//	
//	public static void setUp(Context ctx) {
//		context = ctx;
//		serviceConnection = new ServiceConnection() {
//			
//			/**
//			 * Called when the service has been connected. 
//			 */
//			public void onServiceConnected(ComponentName name, IBinder service) {
//				// As we know exactly what service is used, we can cast it directly.
//				audioService = ((AudioService.LocalBinder)service).getService();
//				
//				//audioService.setSongReader(songReader);
//				audioService.startService(new Intent(context, AudioService.class));
//				
//				// Little notification, just for fun.
//				//Toast.makeText(FirstActivity.this, getText(R.string.notificationTitle), Toast.LENGTH_SHORT);
//			}
//			
//			/**
//			 * Called when the service has been unexpectedly disconnected. This should never happen.
//			 */
//			public void onServiceDisconnected(ComponentName name) {
//				audioService.stopService(new Intent(context, AudioService.class));
//				audioService = null;
//			}
//			
//		};
//	}
//	
//	
//	
//	
//	/**
//	 * Establishes a connection with the explicit service.
//	 */
//	public static void bindService() {
//		if (!isBound) {
//			isBound = context.bindService(new Intent(context, AudioService.class), serviceConnection, Context.BIND_AUTO_CREATE);
//			if (!isBound) {
//				Log.e("XXX", "Service NON lancé !");
//			}
//		}
//	}
//	
//	/**
//	 * Detaches our connection with the service.
//	 */
//	public static void unbindService() {
//		if (isBound) {
//			Log.e("XXX", "FirstActivity.doUnBindService");
//			context.unbindService(serviceConnection);
//			isBound = false;
//		}
//	}
}
