package aks.jnv.task;

import java.io.File;

import aks.jnv.adapter.MusicSelectionItem;
import aks.jnv.adapter.MusicSelectionMusicItem;
import aks.jnv.song.SongFormat;
import aks.jnv.song.SongUtil;
import android.os.AsyncTask;

/**
 * Task that finds music from the given folder only, and notifies for every sub-folder or music found.
 * Music are considered "found" only if its format has been validated.
 * 
 * The validation of a music is only based on its extension.
 * 
 * @author Julien Névo
 *
 */
public class FindMusicTask extends AsyncTask<Void, MusicSelectionItem, Void> {

	/**
	 * Interface for object wanting to be notified when a music or a folder has been found by the FindMusicTask.
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
		 * Notifies that an item has been found.
		 * @param item information about the item found.
		 */
		public void onItemFound(MusicSelectionItem item);

		/**
		 * Notifies that the music search has finished.
		 */
		public void onMusicSearchFinished();

	}
	
	/** The debug tag of this class. */
	//private static final String DEBUG_TAG = "FindMusicTask";
	
	/** The path from where to find the music. */
	private String mBasePath;
	/** A Context. */
	//private Context mContext;
	
	/** Possible callback about result of the music search. */
	private IFindMusicTaskCallback mCallback;

	public FindMusicTask(/*Context context, */ String basePath, IFindMusicTaskCallback callback) {
		//mContext = context;
		mBasePath = basePath;
		mCallback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		// Notifies that the works starts.
		if (mCallback != null) {
			mCallback.onMusicSearchStarted();
		}
	}
	
	@Override
	protected void onProgressUpdate(MusicSelectionItem... values) {
		super.onProgressUpdate(values);
		
		mCallback.onItemFound(values[0]);
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		// Finds all the music and folder from the base path, and notifies the callback.
		File path = new File(mBasePath);
		findMusic(path);
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		// Notifies that the works starts.
		if (mCallback != null) {
			mCallback.onMusicSearchFinished();
		}
	}

	/**
	 * Method that finds all the music and folders of the given path, notifies the callback if one is valid.
	 * @param path The current path.
	 */
	private void findMusic(File path) {
		// If no callback is present, nothing has to be done.
		if (mCallback == null) {
			return;
		}
		
		// Gets all the files of the folder.
		File[] files = path.listFiles();
		
		// Scans all of them. They may be directories.
		if (files != null) {
			for (File file : files) {
				// Stops the task if it was cancelled.
				if (isCancelled()) {
					return;
				}
				
				if (file.isDirectory()) {
					// It's a directory. Notifies it, but don't explore it.
					publishProgress(new MusicSelectionItem(file.getAbsolutePath(), true));
				} else {
					// Found a file. Is it a music?
					SongFormat songFormat = SongUtil.getFileFormat(file);
					if (songFormat != SongFormat.unknown) {
						// Notifies the callback that a music has been found.
						publishProgress(new MusicSelectionMusicItem(file.getAbsolutePath(), songFormat));
					}
				}
			}
		}
		
	}

}
