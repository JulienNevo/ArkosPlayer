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
