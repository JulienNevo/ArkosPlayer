package aks.jnv.audio;

/**
 * Interface used by components that want to be notified when the values of the equalizer have changed.
 * 
 * @author Julien Névo
 *
 */
public interface IEqualizerObserver {

	/**
	 * The subject notifies that the values of the equalizer have changed. 
	 * @param channel1Volume The volume for the channel 1.
	 * @param channel2Volume The volume for the channel 2.
	 * @param channel3Volume The volume for the channel 3.
	 * @param noise The noise.
	 */
	void notifyNewEqualizerValues(int channel1Volume, int channel2Volume, int channel3Volume, int noise);
}
