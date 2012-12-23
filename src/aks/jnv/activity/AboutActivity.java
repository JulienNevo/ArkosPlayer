package aks.jnv.activity;

import android.os.Bundle;

/**
 * The About Activity, showing the credits.
 * 
 * @author Julien Névo
 *
 */
public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected boolean isMenuAboutAvailable() {
		// Obviously, the About button isn't available in the About Activity.
		return false;
	}
}
