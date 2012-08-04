package aks.jnv.task;

import java.io.File;

import aks.jnv.adapter.MusicItem;
import aks.jnv.song.SongFormat;
import aks.jnv.song.SongUtil;
import android.os.AsyncTask;

/**
 * Task that finds music from the given folder and inside the sub-folders, and notifies for every music found, once its format has been validated.
 * 
 * @author Julien Névo
 *
 */
public class FindMusicTask extends AsyncTask<Void, MusicItem, Void> {

	/** The debug tag of this class. */
	//private static final String DEBUG_TAG = "FindMusicTask";
	
	/** The path from where to find the music. */
	private String mBasePath;
	/** A Context. */
	//private Context mContext;
	
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
	protected void onProgressUpdate(MusicItem... values) {
		super.onProgressUpdate(values);
		
		// Notifies the possible callback that a music has been found.
		if (mCallback != null) {
			mCallback.onMusicFound(values[0]);
		}
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		// Finds all the music from the base path, recursively, and notifies the callback.
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
	 * Recursive method that finds all the music of the given path, notifies the callback if one is valid, and searches the possible sub-folders.
	 * @param path The current path.
	 */
	private void findMusic(File path) {

		// Gets all the files of the folder.
		File[] files = path.listFiles();
		
		// Scans all of them. They may be directories, in which case we explore them recursively.
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					// It's a directory. Explores it recursively.
					findMusic(file);
				} else {
					// Found a file. Is it a music?
					SongFormat songFormat = SongUtil.getFileFormat(file);
					if (songFormat != SongFormat.unknown) {
						// If yes, notifies the possible callback about it.
						publishProgress(new MusicItem[] { new MusicItem(file.getAbsolutePath(), songFormat) });
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}
				}
			}
		}
		
	}

}
