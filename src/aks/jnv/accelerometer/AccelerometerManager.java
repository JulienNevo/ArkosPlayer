package aks.jnv.accelerometer;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Accelerometer Manager.
 * 
 * Can't be instantiated, use the static methods instead.
 * 
 * @author Julien Névo
 * 
  */
public class AccelerometerManager {

	/** Context in which is used the accelerometer. */
	private static Context context;
	
	/** SensorManager useful to unregister the listener. */
	private static SensorManager sensorManager;
	
	/** Sensor representing the hardware accelerometer used. */
	private static Sensor sensor;
	
	/** The Accelerometer Event listener. */
	private static AccelerometerEventListener sensorEventListener = new AccelerometerEventListener();
	
	/** Indicates whether the sensor is active. */
	private static boolean isSensorActive = false;

	/**
	 * Indicates whether the sensor is active.
	 * @return true if the sensor is active.
	 */
	public static boolean isSensorActive() {
		return isSensorActive;
	}
	
	/**
	 * Private constructor so that no instance can be created.
	 */
	private AccelerometerManager() { }
	
	/**
	 * Sets the context. This should be only done once so that the manager can get a handle on an hardware accelerometer.
	 * @param context the context in which the accelerometer is used.
	 */
	public static void setContext(Context context) {
		AccelerometerManager.context = context;
	}
	
	/**
	 * Indicates if an accelerometer is present in this device.
	 * @return true if an accelerometer is present in this device.
	 */
	public static boolean isAccelerometerPresent() {
		
		// We may already have found a sensor.
		if (sensor != null) {
			return true;
		}
		
		return (getAccelerometer() != null);
	}
	
	/**
	 * Returns the first accelerometer present, or null if none is available.
	 * @return the first accelerometer present, or null if none is available.
	 */
	private static Sensor getAccelerometer() {
		
		// We may already have found a sensor.
		if (sensor != null) {
			return sensor;
		}
		
		if (context != null) {
			sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
			if (sensorManager != null) {
				List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
				if (list.size() > 0) {
					// The first sensor found will do.
					sensor = list.get(0);
				}
			}
		}
		
		return sensor;
	}

	/**
	 * Makes a listener get events from the accelerometer present, if any.
	 * @param listener the listener that will get the accelerometer events.
	 * @return true if the sensor is active, and so if the listener has been connected.
	 */
	public static boolean startListening(IAccelerometerListener listener) {
		isSensorActive = sensorManager.registerListener(sensorEventListener, getAccelerometer(), SensorManager.SENSOR_DELAY_GAME);
		sensorEventListener.setListenerToAccelerometer(listener);
		
		return isSensorActive;
	}

	/**
	 * Makes the listener currently listening to the accelerometer, if any, stop listening.
	 */
	public static void stopListening() {
		if ((sensorManager != null) && (sensorEventListener != null)) {
			sensorManager.unregisterListener(sensorEventListener);
			isSensorActive = false;
		}
		
	}
	
}
