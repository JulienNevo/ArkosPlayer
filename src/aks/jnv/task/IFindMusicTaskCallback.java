package aks.jnv.task;

import aks.jnv.adapter.MusicItem;

/**
 * Interface for object wanting to be notified when a music has been found by the FindMusicTask, and also about its start and end.
 * 
 * @author Julien Névo
 *
 */
public interface IFindMusicTaskCallback {

	/**
	 * Notifies that the music search has started.
	 */
	public void onMusicSearchStarted();
	
	/**
	 * Notifies that a music has been found.
	 * @param musicItem Information about the music found.
	 */
	public void onMusicFound(MusicItem musicItem);

	/**
	 * Notifies that the music search has finished.
	 */
	public void onMusicSearchFinished();

}