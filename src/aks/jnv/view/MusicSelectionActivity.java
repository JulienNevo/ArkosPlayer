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

package aks.jnv.view;

import aks.jnv.R;
import aks.jnv.adapter.MusicItem;
import aks.jnv.adapter.MusicSelectionAdapter;
import aks.jnv.song.SongFormat;
import aks.jnv.task.FindMusicTask;
import aks.jnv.task.IFindMusicTaskCallback;
import aks.jnv.util.FileUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Activity in which the user can select the music to listen to. Also builds the list of Music to play, in every format.
 * 
 * @author Julien Névo.
 *
 */
public class MusicSelectionActivity extends Activity implements IFindMusicTaskCallback, OnItemClickListener {

	/** The debug tag of this class. */
	//private static final String DEBUG_TAG = MusicSelectionActivity.class.getSimpleName();

	/** The Task that finds music. */
	private FindMusicTask mTask;
	
	/** Adapter that fills the ListView with music files. */
	private MusicSelectionAdapter mAdapter;
	/** The ListView. */
	private ListView mListView;
	
	/** Indicates if all the music were found, so that we don't search again if coming back to this Activity. */
	private boolean mMusicSearchFinished;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Sets the ProgressBar.
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminate(true);
		
		setContentView(R.layout.music_selection);
		
		// Sets up the ListView and the Adapter.
		mListView = (ListView)findViewById(R.id.music_selection_listView);
		mAdapter = new MusicSelectionAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Starts the Task that finds music, unless all the music has been found before.
		if ((!mMusicSearchFinished) && (mTask == null)) {
			mTask = new FindMusicTask(FileUtils.MUSIC_FOLDER, this);
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
		mAdapter.setNewDataCanCome(true);
		setProgressBarIndeterminateVisibility(true);
	}
	
	@Override
	public void onMusicFound(MusicItem musicItem) {
		mAdapter.add(musicItem);
	}

	@Override
	public void onMusicSearchFinished() {
		mAdapter.setNewDataCanCome(false);
		setProgressBarIndeterminateVisibility(false);
		
		// No need to search anymore when coming back to this Activity.
		mMusicSearchFinished = true;
	}

	// -----------------------------------------
	// OnItemClickListener method implementation
	// -----------------------------------------
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// An item has been clicked. Gets the music related to it, if the format is known.
		MusicItem musicItem = (MusicItem)mAdapter.getItem(position);
		if (musicItem.getSongFormat() != SongFormat.unknown) {
			// Sends the music to the PlayMusicActivity.
			//Log.e(DEBUG_TAG, musicItem.getPath());
			Intent intent = new Intent(this, PlayMusicActivity.class);
			intent.putExtra(PlayMusicActivity.EXTRA_SONG_NAME, musicItem.getPath());
			startActivity(intent);
		}
	}
}
