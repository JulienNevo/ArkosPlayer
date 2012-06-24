package aks.jnv.view;

import java.io.File;

import aks.jnv.R;
import aks.jnv.audio.AudioService;
import aks.jnv.audio.AudioServiceContainer;
import aks.jnv.audio.AudioService.LocalBinder;
import aks.jnv.file.FileManager;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * TODO :
 * 
 * - Make this class derives from a ServiceListActivity, or a ServiceActivity that would have a
 *   List...
 * 
 * 
 * @author Julien Névo
 *
 */
public class MusicChoiceActivity extends ListActivity {

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
		Object o = this.getListAdapter().getItem(position);
		SongItem si = (SongItem)o;
		File songFile = si.songFile;
		
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
		Intent intent = new Intent(MusicChoiceActivity.this, PlayMusicActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
