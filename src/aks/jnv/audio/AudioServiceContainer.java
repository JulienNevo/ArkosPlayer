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
