package aks.jnv.activity;

import aks.jnv.R;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * A base Activity for all the Activity of the Application.
 * 
 * @author Julien Névo
 *
 */
public class BaseActivity extends Activity {

	/**
	 * Indicates whether the "About" button is available in the menu.
	 * @return True if the "About" button is available in the menu.
	 */
	protected boolean isMenuAboutAvailable() {
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Creates the menu.
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		
		// Makes the "About" menu visible according to the Activity.
		menu.findItem(R.id.menu_about).setVisible(isMenuAboutAvailable());
		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_about: {
			// About selected. Simply runs the About Activity.
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		case R.id.menu_preferences: {
			// Manual Configuration selected. Simply runs the Manual Configuration Activity.
			Intent intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			return true;
		}
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
}
