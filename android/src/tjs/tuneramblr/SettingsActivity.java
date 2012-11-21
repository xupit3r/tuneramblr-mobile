package tjs.tuneramblr;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Responsible for handling user settings changes.
 */
public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
