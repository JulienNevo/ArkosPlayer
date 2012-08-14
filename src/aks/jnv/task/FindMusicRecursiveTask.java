//package aks.jnv.task;
//
//import java.io.File;
//
//import aks.jnv.adapter.MusicSelectionMusicItem;
//import aks.jnv.song.SongFormat;
//import aks.jnv.song.SongUtil;
//import android.os.AsyncTask;
//
///**
// * Task that finds music from the given folder and inside the sub-folders, and notifies for every music found, once its format has been validated.
// * 
// * @author Julien Névo
// *
// */
//public class FindMusicRecursiveTask extends AsyncTask<Void, MusicSelectionMusicItem, Void> {
//
//	/**
//	 * Interface for object wanting to be notified when a music has been found by the FindMusicTask, and also about its start and end.
//	 * 
//	 * @author Julien Névo
//	 *
//	 */
//	public interface IFindMusicRecursiveTaskCallback {
//
//		/**
//		 * Notifies that the music search has started.
//		 */
//		public void onMusicSearchStarted();
//		
//		/**
//		 * Notifies that a music has been found.
//		 * @param musicItem Information about the music found.
//		 */
//		public void onMusicFound(MusicSelectionMusicItem musicItem);
//
//		/**
//		 * Notifies that the music search has finished.
//		 */
//		public void onMusicSearchFinished();
//
//	}
//	
//	/** The debug tag of this class. */
//	//private static final String DEBUG_TAG = "FindMusicTask";
//	
//	/** The path from where to find the music. */
//	private String mBasePath;
//	/** A Context. */
//	//private Context mContext;
//	
//	/** Possible callback about result of the music search. */
//	private IFindMusicRecursiveTaskCallback mCallback;
//
//	public FindMusicRecursiveTask(/*Context context, */ String basePath, IFindMusicRecursiveTaskCallback callback) {
//		//mContext = context;
//		mBasePath = basePath;
//		mCallback = callback;
//	}
//	
//	@Override
//	protected void onPreExecute() {
//		super.onPreExecute();
//		
//		// Notifies that the works starts.
//		if (mCallback != null) {
//			mCallback.onMusicSearchStarted();
//		}
//	}
//	
//	@Override
//	protected void onProgressUpdate(MusicSelectionMusicItem... values) {
//		super.onProgressUpdate(values);
//		
//		// Notifies the possible callback that a music has been found.
//		if (mCallback != null) {
//			mCallback.onMusicFound(values[0]);
//		}
//	}
//	
//	@Override
//	protected Void doInBackground(Void... arg0) {
//		
//		// Finds all the music from the base path, recursively, and notifies the callback.
//		File path = new File(mBasePath);
//		findMusic(path);
//		
//		return null;
//	}
//
//	@Override
//	protected void onPostExecute(Void result) {
//		super.onPostExecute(result);
//		
//		// Notifies that the works starts.
//		if (mCallback != null) {
//			mCallback.onMusicSearchFinished();
//		}
//	}
//
//	/**
//	 * Recursive method that finds all the music of the given path, notifies the callback if one is valid, and searches the possible sub-folders.
//	 * @param path The current path.
//	 */
//	private void findMusic(File path) {
//
//		// Gets all the files of the folder.
//		File[] files = path.listFiles();
//		
//		// Scans all of them. They may be directories, in which case we explore them recursively.
//		if (files != null) {
//			for (File file : files) {
//				if (file.isDirectory()) {
//					// It's a directory. Explores it recursively.
//					findMusic(file);
//				} else {
//					// Found a file. Is it a music?
//					SongFormat songFormat = SongUtil.getFileFormat(file);
//					if (songFormat != SongFormat.unknown) {
//						// If yes, notifies the possible callback about it.
//						publishProgress(new MusicSelectionMusicItem[] { new MusicSelectionMusicItem(file.getAbsolutePath(), songFormat) });
//					}
//				}
//			}
//		}
//		
//	}
//
//}
