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

package aks.jnv.activity;

import java.io.File;

import aks.jnv.R;
import aks.jnv.adapter.MusicSelectionItem;
import aks.jnv.adapter.MusicSelectionMusicItem;
import aks.jnv.adapter.MusicSelectionAdapter;
import aks.jnv.song.SongFormat;
import aks.jnv.task.FindMusicTask;
import aks.jnv.task.FindMusicTask.IFindMusicTaskCallback;
import aks.jnv.util.FileUtils;
import aks.jnv.util.PreferenceUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Activity in which the user can select the music to listen to. Also builds the list of Music to play, in every format.
 * 
 * @author Julien Névo.
 *
 */
public class MusicSelectionActivity extends Activity implements IFindMusicTaskCallback, OnItemClickListener {

	/** The debug tag of this class. */
	//private static final String DEBUG_TAG = MusicSelectionActivity.class.getSimpleName();

	/** The tag used to give this Activity the name of the folder from where to start the search. */
	private static final String EXTRA_MUSIC_PATH = "MUSIC_PATH";

	/** The Task that finds music. */
	private FindMusicTask mTask;
	
	/** Adapter that fills the ListView with music files. */
	private MusicSelectionAdapter mAdapter;
	/** The ListView. */
	private ListView mListView;
	/** The TexView showing the current folder. */
	private TextView mCurrentFolderTextView;
	/** The ImageButton to go to the parent folder. */
	private ImageButton mGoToParentFolderImageButton;
	/** A progress bar. */
	private ProgressBar mProgressBar;
	
	/** Indicates if all the music were found, so that we don't search again if coming back to this Activity. */
	private boolean mMusicSearchFinished;
	
	/** The folder from where to start the search. */
	private String mCurrentFolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Removed the title bar.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_music_selection);
		
		mCurrentFolderTextView = (TextView)findViewById(R.id.music_selection_current_folder_textview);
		mGoToParentFolderImageButton = (ImageButton)findViewById(R.id.music_selection_go_to_parent_folder_imagebutton);
		mProgressBar = (ProgressBar)findViewById(R.id.music_selection_pressbar);
		
		// Gets the possible given music path to this Activity.
		// Uses a default folder if none is given.
		// Also saves this folder in the Shared Preferences.
		String musicPath = getIntent().getStringExtra(EXTRA_MUSIC_PATH);
		mCurrentFolder = (musicPath == null) ? FileUtils.MUSIC_FOLDER : musicPath;
		PreferenceUtils.setCurrentMusicFolder(this, mCurrentFolder);
		
		// Sets up the ListView and the Adapter.
		mListView = (ListView)findViewById(R.id.music_selection_listView);
		mAdapter = new MusicSelectionAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		// Updates the current folder TextView.
		updateCurrentFolderTextViewAndParentFolderButton();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Starts the Task that finds music, unless all the music has been found before.
		if ((!mMusicSearchFinished) && (mTask == null)) {
			mTask = new FindMusicTask(mCurrentFolder, this);
			Void params = null;
			mTask.execute(params);
		}
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Cancels the Task before leaving.
		if (mTask != null) {
			mTask.cancel(true);
			mTask = null;
		}
	}
	
	
	// ---------------------------------------------
	// IFindMusicTaskCallback methods implementation
	// ---------------------------------------------
	
	@Override
	public void onMusicSearchStarted() {
		//mAdapter.setNewDataCanCome(true);
		//setProgressBarIndeterminateVisibility(true);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	

	@Override
	public void onItemFound(MusicSelectionItem item) {
		mAdapter.add(item);
	}

	@Override
	public void onMusicSearchFinished() {
		//mAdapter.setNewDataCanCome(false);
		mAdapter.setLastDataArrived();
		//setProgressBarIndeterminateVisibility(false);
		mProgressBar.setVisibility(View.GONE);
		
		// No need to search anymore when coming back to this Activity.
		mMusicSearchFinished = true;
		
		mAdapter.notifyDataSetChanged();
	}

	// -----------------------------------------
	// OnItemClickListener method implementation
	// -----------------------------------------
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// An item has been clicked. Gets the music related to it, if the format is known,
		// or explores the folder if it was one.
		MusicSelectionItem item = (MusicSelectionItem)mAdapter.getItem(position);
		if (item.isFolder()) {
			// Launches the same Activity with the path of the selected folder.
			Intent intent = new Intent(this, MusicSelectionActivity.class);
			intent.putExtra(EXTRA_MUSIC_PATH, item.getPath());
			startActivity(intent);
		} else {
			MusicSelectionMusicItem musicItem = (MusicSelectionMusicItem)item;
			if (musicItem.getSongFormat() != SongFormat.unknown) {
				// Sends the music to the PlayMusicActivity.
				goToPlayMusicActivity(musicItem.getPath());
			}
		}
	}
	
	// ---------------------------------
	// Methods defined in the Layout XML
	// ---------------------------------

	/**
	 * A click has been performed on the Go To Parent Folder button.
	 * @param view The View.
	 */
	public void onGoToParentFolderButtonClick(View view) {
		// Gets the parent folder, if any.
		File file = new File(mCurrentFolder);
		String newFolder = file.getParent();
		if (newFolder != null) {
			Intent intent = new Intent(this, MusicSelectionActivity.class);
			intent.putExtra(EXTRA_MUSIC_PATH, newFolder);
			startActivity(intent);
		}
	}
	
	/**
	 * A click has been performed on the Go To Parent Folder button.
	 * @param view The View.
	 */
	public void onGoToPlayMusicActivityButtonClick(View view) {
		goToPlayMusicActivity(null);
	}
	
	// ---------------
	// Private methods
	// ---------------

	/**
	 * Updates the current folder TextView and the parent folder button.
	 */
	private void updateCurrentFolderTextViewAndParentFolderButton() {
		mCurrentFolderTextView.setText(mCurrentFolder);
		// Is there a parent? If no, disables the icon to go to the parent folder.
		File file = new File(mCurrentFolder);
		mGoToParentFolderImageButton.setEnabled(file.getParent() != null);
	}
	
	/**
	 * Goes to the Play Music Activity.
	 * @param songName The song to play, or null if none should be played.
	 */
	private void goToPlayMusicActivity(String songName) {
		Intent intent = new Intent(this, PlayMusicActivity.class);
		// Don't indicate the song name if there isn't any.
		if (songName != null) {
			intent.putExtra(PlayMusicActivity.EXTRA_SONG_NAME, songName);
		}
		startActivity(intent);
	}
}
