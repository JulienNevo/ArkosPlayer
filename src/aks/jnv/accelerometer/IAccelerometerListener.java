package aks.jnv.accelerometer;

/**
 * Interface to the Accelerometer Listener.
 * 
 * @author Julien Névo
 *
 */
public interface IAccelerometerListener {

	/**
	 * Fired when the accelerometer indicates a change.
	 * @param x the new X value.
	 * @param y the new Y value.
	 * @param z the new Z value.
	 */
	public void onAccelerationChanged(float x, float y, float z);
}
