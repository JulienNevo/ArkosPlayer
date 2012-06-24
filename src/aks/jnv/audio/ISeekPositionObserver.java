package aks.jnv.audio;

/**
 * Interface used by components that want to be notified when the seek position, in seconds, has changed.
 * 
 * @author Julien Névo
 *
 */
public interface ISeekPositionObserver {

	/**
	 * The subject notifies that a seek position has been changed. 
	 * @param seekPosition the new seek position.
	 */
	void notifyNewSeekPositionFromSubject(int seekPosition);
	
}
