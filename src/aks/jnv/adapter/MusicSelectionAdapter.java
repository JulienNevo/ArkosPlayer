package aks.jnv.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import aks.jnv.R;
import aks.jnv.song.SongFormat;
import aks.jnv.util.FileUtils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Adapter that displays the music files found.
 * 
 * The music are read from the SDCard in an ASyncThread. Since we want the list to be sorted, we sort it, but not on every addition, as it would probably be
 * too slow, so we use a Thread to add the recently stored entries into the "real" list and sort it.
 * 
 * Watch out for calling setNewDataCanCome before and after all the entries are added, in order to start/stop the sorting thread.
 * 
 * A trick is used for the separators: we detect them as they are not music: they haven't the right extension nor an existing file, so have an "unknown" format.
 * 
 * @author Julien Névo
 *
 */
public class MusicSelectionAdapter extends BaseAdapter {

	/** The debug tag of this class. */
	private static final String DEBUG_TAG = "MusicSelectionAdapter";
	
	/** The digit separator. */
	private static final String DIGIT_SEPARATOR = "0-9";
	
	/** Duration is ms between the sorts. */
	protected static final long DURATION_BETWEEN_SORTS = 300;

	/** The data to display. */
	private ArrayList<MusicItem> mData = new ArrayList<MusicItem>();
	
	/** The data added but yet to be added to the "real" data. */
	private ArrayList<MusicItem> mDataPending = new ArrayList<MusicItem>();
	
	/** Inflater to show the items. */
	private LayoutInflater mInflater;
	
	/** Cached Drawable for the YM icon. */
	private static Drawable mDrawableYM;
	/** Cached Drawable for the AKS icon. */
	private static Drawable mDrawableAKS;
	/** Cached Drawable for the SKS icon. */
	private static Drawable mDrawableSKS;
	
	/** Indicates if the list must be sorted. Should only be written with the synchronized setIsSortingDirty() method. */
	private boolean mIsSortingDirty;
	
	/** Indicates if the sort must continue. Should be set to false when no more data is added. */
	private boolean mMustContinueSortThread = true;
	
	/** Handler used to notify a change of data set from the UI. */
	private Handler mHandler;

	/** A Runnable used every time the DataSet has changed. It is used in order to be executed on the UI Thread. */
	private Runnable mNotifyDataSetChangedRunnable;
	
	/** Letters in upper case which separator are activated. */
	//private ArrayList<Character> mSeparatorLettersActivated = new ArrayList<Character>();
	/** True if anything that an upper case letter has been found. Will trigger the 0-9 separator. */
	//private boolean isOtherLettersSeparatorActivated;
	private HashSet<String> mSeparatorLettersActivated = new HashSet<String>();
	
	/**
	 * Constructor.
	 * @param context A Context.
	 */
	public MusicSelectionAdapter(Context context) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHandler = new Handler();
		
		// Caches the Drawables if needed.
		if (mDrawableYM == null) {
			Resources resources = context.getResources();
			mDrawableYM = resources.getDrawable(R.drawable.ic_launcher);
			mDrawableAKS = resources.getDrawable(R.drawable.icon_aks);
			mDrawableSKS = resources.getDrawable(R.drawable.icon_sks);
		}
		
		// Creates the separators.
//		MusicItem separator = new MusicItem(DIGIT_SEPARATOR, SongFormat.unknown);
//		mDataPending.add(separator);
//		for (char c = 'A'; c <= 'Z'; c++) {
//			separator = new MusicItem(Character.toString(c), SongFormat.unknown);
//			mDataPending.add(separator);
//		}

		// Creates a Runnable in order the data set to be notified on the UI Thread by the Handler.
		// Created here once because may be useful several times.
		mNotifyDataSetChangedRunnable = new Runnable() {
			
			@Override
			public void run() {
				MusicSelectionAdapter.this.notifyDataSetChanged();				
			}
		};
		
		// Creates the Thread that will regularly sort the collection.
		Thread sortThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// As long as needed
				while (mMustContinueSortThread) {
					try {
						Thread.sleep(DURATION_BETWEEN_SORTS);
						if (mIsSortingDirty) {
							addPendingDataAndSortData();
						}
					} catch (InterruptedException e) {
						Log.e(DEBUG_TAG, e.getMessage());
					}
				}
			}
		});
		
		sortThread.start();
	}
	
	/**
	 * Indicates if new data can come.
	 * @param newDataCanCome True if new data can come.
	 */
	public void setNewDataCanCome(boolean newDataCanCome) {
		mMustContinueSortThread = newDataCanCome;
		// If the sorting is over, we sort for the last time in case the thread couldn't do it.
		if (!newDataCanCome) {
			addPendingDataAndSortData();
		}
	}
	
	/**
	 * Adds a music item to the list. Only items with valid format should be added, else they are considered separators.
	 * @param musicItem The music item to add.
	 */
	public void add(MusicItem musicItem) {
		// The new entry is actually added to a pending list, waiting to be added to the "real" one by the sorting thread.
		mDataPending.add(musicItem);
		
		// Does it add a new separator ?
		// Gets the upper case of the first (maybe) letter.
		addSeparator(musicItem);
		
		setIsSortingDirty(true);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		// To know if an item is enabled, gets the MusicItem related and checks whether the format is known.
		MusicItem musicItem = (MusicItem)getItem(position);
		return (musicItem.getSongFormat() != SongFormat.unknown);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if (convertView == null) {
			// Inflates the Layout.
			convertView = mInflater.inflate(R.layout.music_selection_item, parent, false);
			
			// Creates a ViewHolder that will hold reference to the View of the item in order to
			// prevent from searching for it every time.
			viewHolder = new ViewHolder();
			viewHolder.textview = (TextView)convertView.findViewById(R.id.music_selection_item_textview);
			viewHolder.layout = (LinearLayout)convertView.findViewById(R.id.music_selection_item_layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		// Sets the View data.
		MusicItem musicItem = (MusicItem)getItem(position);
		
		// Keeps only a short name without the extension.
		String musicShortName = FileUtils.removeExtension(musicItem.getShortName());
		TextView textView = viewHolder.textview;
		LinearLayout layout = viewHolder.layout;
		textView.setText(musicShortName);
		
		// Sets the right image.
		Drawable drawable;
		switch (musicItem.getSongFormat()) {
		case YM:
			drawable = mDrawableYM;
			break;
		case AKSBinary:
			drawable = mDrawableAKS;
			break;
		case SKSBinary:
			drawable = mDrawableSKS;
			break;
		default:
			drawable = null;
			break;
		}
		
		textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

		// If no Drawable is found, it is considered a separator.
		if (drawable == null) {
			layout.setGravity(Gravity.CENTER_HORIZONTAL);
			layout.setBackgroundResource(R.color.music_selection_separator_background);
		} else {
			layout.setGravity(Gravity.LEFT);
			layout.setBackgroundResource(android.R.color.black);
		}
		
		return convertView;
	}

	/**
	 * ViewHolder holding references to the Views of an item in order to speed up the look-up.
	 * 
	 * @author Julien Névo.
	 *
	 */
	private static class ViewHolder {
		public LinearLayout layout;
		public TextView textview;
	}
	
	
	// ---------------
	// Private methods
	// ---------------
	
	/**
	 * Sets the IsSortingDirty flag, in a synchronized mode.
	 * @param isSortingDirty True or false.
	 */
	private synchronized void setIsSortingDirty(boolean isSortingDirty) {
		mIsSortingDirty = isSortingDirty;
	}
	
	/**
	 * Appends the pending data and sorts the data and notifies that data set has changed. 
	 */
	private void addPendingDataAndSortData() {
		// Adds the pending data to the "real" data and clears it.
		int size = mDataPending.size();
		setIsSortingDirty(false);
		mData.addAll(mDataPending);
		
		// Removes the entries to the data pending. We make sure only one added are removed, in case another ones were added in between.
		for (int i = 0; i < size; i++) {
			mDataPending.remove(0);
		}
		
		// Sorts the collection.
		Collections.sort(mData);
		
		// Notifies the data set has changed, on the UI Thread.
		mHandler.post(mNotifyDataSetChangedRunnable);
	}
	
	/**
	 * Adds a separator, if needed, according to the music item to add to the list.
	 * @param musicItem The MusicItem to add.
	 */
	private void addSeparator(MusicItem musicItem) {
		char c = musicItem.getShortName().charAt(0);
		String separatorText;
		if (Character.isDigit(c)) {
			// Creates the digit separators.
			separatorText = DIGIT_SEPARATOR;
		} else {
			// Creates a letter (upper case) separator.
			separatorText = Character.toString(Character.toUpperCase(c));
		}
		// Adds the separator only if it wasn't already added.
		if (!mSeparatorLettersActivated.contains(separatorText)) {
			MusicItem separatorItem = new MusicItem(separatorText, SongFormat.unknown);
			mDataPending.add(separatorItem);
			
			mSeparatorLettersActivated.add(separatorText);
		}
	}
}
