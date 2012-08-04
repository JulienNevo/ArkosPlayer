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

import java.io.File;

import aks.jnv.R;
import aks.jnv.file.FileManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * TODO :
 * 
 * - DEPRECATED. REMOVE.
 * 
 * - Make this class derives from a ServiceListActivity, or a ServiceActivity that would have a
 *   List...
 * 
 * 
 * @author Julien Névo
 *
 */
public class MusicChoiceActivityOLD extends ListActivity {

//	private AudioService audioService = null;
//	private ServiceConnection connection = new ServiceConnection() {
//		
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			audioService = null;
//		}
//		
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			audioService = ((AudioService.LocalBinder)service).getService();
//			
//		}
//	};
	
	private static class SongItem {
		public File songFile;
		@Override
		public String toString() {
			return (songFile != null) ? songFile.getName() : "(Not a music file)";
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		File[] files = FileManager.getMusicList();
		
		if (files == null) {
			// FIXME : only for debug for now ! Create an empty Song Item, and will be interpreted as
			// an raw data (from the "raw" dir).
			SongItem[] songItems = new SongItem[1];
			songItems[0] = new SongItem();
			this.setListAdapter(new ArrayAdapter<SongItem>(this, R.layout.rowlayout, R.id.label, songItems));
		} else {
			int nbMusicFiles = files.length;
			SongItem[] songItems = new SongItem[nbMusicFiles];
			for (int i = 0; i < nbMusicFiles; i++) {
				SongItem si = new SongItem();
				si.songFile = files[i];
				songItems[i] = si;
			}
			this.setListAdapter(new ArrayAdapter<SongItem>(this, R.layout.rowlayout, R.id.label, songItems));
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Gets the item that was clicked.
		//Object o = this.getListAdapter().getItem(position);
		//SongItem si = (SongItem)o;
		//File songFile = si.songFile;
		
		//AudioService audioService = FirstActivity.getAudioService();
		//AudioService audioService = AudioService.getInstance();
		
		
		//AudioService.LocalBinder binder = new LocalBinder();
		
		// Gets the audio Service.
		//audioService.playSong(songFile);
		//AudioServiceContainer.getAudioService().playSong(songFile);
		
		// Sets the file to play.
//		AudioService audioService = AudioServiceContainer.getAudioService();
//		((IMusicController)audioService).setSong(songFile);
		
		Bundle bundle = new Bundle();
		Intent intent = new Intent(MusicChoiceActivityOLD.this, PlayMusicActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
