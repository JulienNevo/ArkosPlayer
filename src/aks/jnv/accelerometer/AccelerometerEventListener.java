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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Event Listener of the Accelerometer.
 * 
 * @author Julien Névo
 * 
 */
public class AccelerometerEventListener implements SensorEventListener {

	/** The number of values we store in order to linearize the accelerometer values. Too much give a "fat" motion. */
	private static final int NB_PREVIOUS_VALUES_STORED = 5;

	/** Array storing the previous X positions of the accelerometer. */
	private float[] mPreviousValuesX = new float[NB_PREVIOUS_VALUES_STORED];
	/** Array storing the previous Y positions of the accelerometer. */
	private float[] mPreviousValuesY = new float[NB_PREVIOUS_VALUES_STORED];
	/** Array storing the previous Z positions of the accelerometer. */
	private float[] mPreviousValuesZ = new float[NB_PREVIOUS_VALUES_STORED];

	/** The listener to the accelerometer values change. */
	private IAccelerometerListener mListener;
	
	/**
	 * Indicates what listener to refer to when the accelerometer values change.
	 * @param listener the listener to refer to when the accelerometer values change.
	 */
	public void setListenerToAccelerometer(IAccelerometerListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Linearizes the values of the accelerometer.
		shiftPreviousValuesArrayAndSetNewValue(mPreviousValuesX, event.values[0]);
		shiftPreviousValuesArrayAndSetNewValue(mPreviousValuesY, event.values[1]);
		shiftPreviousValuesArrayAndSetNewValue(mPreviousValuesZ, event.values[2]);
		
		// Inform the listener of the new values.
		if (mListener != null) {
			mListener.onAccelerationChanged(getAverage(mPreviousValuesX), getAverage(mPreviousValuesY), getAverage(mPreviousValuesZ));
		}
	}

	/**
	 * Shifts "to the left" the values of the given array, and set the last element with the given value.
	 * @param values The array to shift.
	 * @param newValue The value to set.
	 */
	private static void shiftPreviousValuesArrayAndSetNewValue(float[] values, float newValue) {
		int length = values.length;
		if (values.length <= 1) {
			return;
		}
		
		for (int i = 1; i < length; i++) {
			values[i - 1] = values[i];
		}
		
		values[length - 1] = newValue;
	}

	/**
	 * Returns the average of the values of the given array.
	 * @param values The array.
	 * @return the average of the values of the given array.
	 */
	private static float getAverage(float[] values) {
		float result = 0;
		int length = values.length;
		for (int i = 0; i < length; i++) {
			result += values[i];
		}
		
		return result / length;
	}
}
