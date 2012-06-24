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
	private float[] previousValuesX = new float[NB_PREVIOUS_VALUES_STORED];
	/** Array storing the previous Y positions of the accelerometer. */
	private float[] previousValuesY = new float[NB_PREVIOUS_VALUES_STORED];
	/** Array storing the previous Z positions of the accelerometer. */
	private float[] previousValuesZ = new float[NB_PREVIOUS_VALUES_STORED];

	/** The listener to the accelerometer values change. */
	private IAccelerometerListener listener;
	
	/**
	 * Indicates what listener to refer to when the accelerometer values change.
	 * @param listener the listener to refer to when the accelerometer values change.
	 */
	public void setListenerToAccelerometer(IAccelerometerListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Linearizes the values of the accelerometer.
		shiftPreviousValuesArrayAndSetNewValue(previousValuesX, event.values[0]);
		shiftPreviousValuesArrayAndSetNewValue(previousValuesY, event.values[1]);
		shiftPreviousValuesArrayAndSetNewValue(previousValuesZ, event.values[2]);
		
		// Inform the listener of the new values.
		if (listener != null) {
			listener.onAccelerationChanged(getAverage(previousValuesX), getAverage(previousValuesY), getAverage(previousValuesZ));
		}
	}

	/**
	 * Shift "to the left" the values of the given array, and set the last element with the given value.
	 * @param values the array to shift.
	 * @param newValue the value to set.
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
	 * @param values the array.
	 * @return the average of the values of the given array.
	 */
	private float getAverage(float[] values) {
		float result = 0;
		int length = values.length;
		for (int i = 0; i < length; i++) {
			result += values[i];
		}
		
		return result / length;
	}
}
