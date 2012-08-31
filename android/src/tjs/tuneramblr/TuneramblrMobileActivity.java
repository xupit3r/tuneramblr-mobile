/*
 * Copyright 2012 Joe D'Alessandro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tjs.tuneramblr;

import java.io.IOException;

import tjs.tuneramblr.data.UserInfoDS;
import tjs.tuneramblr.dialogs.AddSongDialog;
import tjs.tuneramblr.meta.location.LastLocationFinder;
import tjs.tuneramblr.meta.location.base.ILastLocationFinder;
import tjs.tuneramblr.meta.location.base.LocationRequester;
import tjs.tuneramblr.meta.location.utils.PlatformSpecificImplementationFactory;
import tjs.tuneramblr.meta.model.UserInfo;
import tjs.tuneramblr.receivers.LocationChangedReceiver;
import tjs.tuneramblr.receivers.NewCheckinReceiver;
import tjs.tuneramblr.receivers.PassiveLocationChangedReceiver;
import tjs.tuneramblr.services.LoginService;
import tjs.tuneramblr.util.base.SharedPreferenceSaver;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * This is the main activity for tuneramblr mobile
 * 
 * Portions of the code that follows was adapted from code samples found at:
 * http://blog.radioactiveyak.com/2011/06/deep-dive-into-location-part-2-being
 * .html
 */
public class TuneramblrMobileActivity extends FragmentActivity {

	// tag for logging
	protected static String TAG = "TuneramblrMobileActivity";

	// managers
	protected PackageManager packageManager;
	protected NotificationManager notificationManager;
	protected LocationManager locationManager;

	// preference managers
	protected SharedPreferences prefs;
	protected Editor prefsEditor;
	protected SharedPreferenceSaver sharedPreferenceSaver;

	// general android criteria
	protected Criteria criteria;

	// UI fragments
	protected CheckinFragment checkinFragment;
	protected LoginFragment loginFragment;

	// location members
	protected ILastLocationFinder lastLocationFinder;
	protected LocationRequester locationUpdateRequester;
	protected PendingIntent locationListenerPendingIntent;
	protected PendingIntent locationListenerPassivePendingIntent;

	// checkin stuff
	protected IntentFilter newCheckinFilter;
	protected ComponentName newCheckinReceiverName;

	// these are totally some IDs for various dialogs we want to show
	protected static final int ADD_SONG_PROGRESS_DIALOG = 0;
	protected static final int COLOR_PICKER_DIALOG = 1;

	// image capture response code
	protected static final int TAKE_PHOTO_CODE = 100;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the current view
		setContentView(R.layout.main);

		// retrieve the fragments
		checkinFragment = new CheckinFragment();
		loginFragment = new LoginFragment();

		// determine which fragment we want to show
		UserInfoDS userInfoDs = new UserInfoDS(getApplicationContext());
		UserInfo userInfo = null;
		try {
			userInfo = userInfoDs.readUserInfo();
		} catch (IOException e) {
			userInfo = null;
		}

		Fragment fragment = null;
		if ((userInfo != null) && (userInfo.getUsername() != null)
				&& (userInfo.getPassword() != null)) {
			fragment = checkinFragment;
		} else {
			fragment = loginFragment;
		}

		// add the fragment to the the main layout
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, fragment).commit();

		// Get references to the managers
		packageManager = getPackageManager();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Get a reference to the Shared Preferences and a Shared Preference
		// Editor.
		prefs = getSharedPreferences(
				TuneramblrConstants.SHARED_PREFERENCE_FILE,
				Context.MODE_PRIVATE);
		prefsEditor = prefs.edit();

		// Instantiate a SharedPreferenceSaver class based on the available
		// platform version.
		// This will be used to save shared preferences
		sharedPreferenceSaver = PlatformSpecificImplementationFactory
				.getSharedPreferenceSaver(this);

		// Save that we've been run once.
		prefsEditor.putBoolean(TuneramblrConstants.SP_KEY_RUN_ONCE, true);
		sharedPreferenceSaver.savePreferences(prefsEditor, false);

		// Specify the Criteria to use when requesting location updates while
		// the application is Active
		criteria = new Criteria();
		if (TuneramblrConstants.USE_GPS_WHEN_ACTIVITY_VISIBLE)
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
		else
			criteria.setPowerRequirement(Criteria.POWER_LOW);

		// Setup the location update Pending Intents
		Intent activeIntent = new Intent(this, LocationChangedReceiver.class);
		locationListenerPendingIntent = PendingIntent.getBroadcast(this, 0,
				activeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent passiveIntent = new Intent(this,
				PassiveLocationChangedReceiver.class);
		locationListenerPassivePendingIntent = PendingIntent.getBroadcast(this,
				0, passiveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Instantiate a LastLocationFinder class.
		// This will be used to find the last known location when the
		// application starts.
		lastLocationFinder = PlatformSpecificImplementationFactory
				.getLastLocationFinder(this);
		lastLocationFinder
				.setChangedLocationListener(oneShotLastLocationUpdateListener);

		// Instantiate a Location Update Requester class based on the available
		// platform version.
		// This will be used to request location updates.
		locationUpdateRequester = PlatformSpecificImplementationFactory
				.getLocationRequester(locationManager);

		// Create an Intent Filter to listen for checkins
		newCheckinReceiverName = new ComponentName(this,
				NewCheckinReceiver.class);
		newCheckinFilter = new IntentFilter(
				TuneramblrConstants.NEW_CHECKIN_ACTION);

		// check if anything has changed
		if (getIntent().hasExtra(TuneramblrConstants.EXTRA_KEY_ID)) {
			// nadda right now
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Commit shared preference that says we're in the foreground.
		prefsEditor.putBoolean(TuneramblrConstants.EXTRA_KEY_IN_BACKGROUND,
				false);
		sharedPreferenceSaver.savePreferences(prefsEditor, false);

		/*
		 * Disable the Manifest Checkin Receiver when the Activity is visible.
		 * The Manifest Checkin Receiver is designed to run only when the
		 * Application isn't active to notify the user of pending checkins that
		 * have succeeded (usually through a Notification). When the Activity is
		 * visible we capture checkins through the checkinReceiver.
		 */
		packageManager.setComponentEnabledSetting(newCheckinReceiverName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);

		// Register the checkinReceiver to listen for checkins while the
		// Activity is visible.
		registerReceiver(checkinReceiver, newCheckinFilter);

		// update UI receiver
		registerReceiver(loginReceiver,
				new IntentFilter(LoginService.LOGGED_IN));

		// Cancel notifications.
		notificationManager.cancel(TuneramblrConstants.CHECKIN_NOTIFICATION);

		// Get the last known location (and optionally request location updates)
		// and
		// update the place list.
		boolean followLocationChanges = prefs.getBoolean(
				TuneramblrConstants.SP_KEY_FOLLOW_LOCATION_CHANGES, true);
		getLocationAndUpdatePlaces(followLocationChanges);
	}

	@Override
	protected void onPause() {
		// Commit shared preference that says we're in the background.
		prefsEditor.putBoolean(TuneramblrConstants.EXTRA_KEY_IN_BACKGROUND,
				true);
		sharedPreferenceSaver.savePreferences(prefsEditor, false);

		/*
		 * Enable the Manifest Checkin Receiver when the Activity isn't active.
		 * The Manifest Checkin Receiver is designed to run only when the
		 * Application isn't active to notify the user of pending checkins that
		 * have succeeded (usually through a Notification).
		 */
		packageManager.setComponentEnabledSetting(newCheckinReceiverName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);

		// Unregister the checkinReceiver when the Activity is inactive.
		unregisterReceiver(checkinReceiver);
		unregisterReceiver(loginReceiver);

		// Stop listening for location updates when the Activity is inactive.
		disableLocationUpdates();

		super.onPause();
	}

	@Override
	protected Dialog onCreateDialog(int dialogId) {
		Dialog theDialog = null;

		switch (dialogId) {
		case ADD_SONG_PROGRESS_DIALOG:
			// P.S. the last parameter (true) means that this dialog is
			// indeterminate. "SongmapMobileActivity.this" is important, because
			// it refers to this activity as the context from which to work
			theDialog = AddSongDialog.show(TuneramblrMobileActivity.this, "",
					"Adding song...", true);
			break;
		case COLOR_PICKER_DIALOG:
			// initialize color picker dialog
			break;
		default:
			// let's just make sure it is null, OK!
			theDialog = null;
		}

		return theDialog;
	}

	/**
	 * Find the last known location (using a {@link LastLocationFinder}) and
	 * updates the place list accordingly.
	 * 
	 * @param updateWhenLocationChanges
	 *            Request location updates
	 */
	protected void getLocationAndUpdatePlaces(boolean updateWhenLocationChanges) {
		// This isn't directly affecting the UI, so put it on a worker thread.
		AsyncTask<Void, Void, Void> findLastLocationTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// Find the last known location, specifying a required accuracy
				// of within the min distance between updates
				// and a required latency of the minimum time required between
				// updates.
				Location lastKnownLocation = lastLocationFinder
						.getLastBestLocation(TuneramblrConstants.MAX_DISTANCE,
								System.currentTimeMillis()
										- TuneramblrConstants.MAX_TIME);
				return null;
			}
		};
		findLastLocationTask.execute();

		// If we have requested location updates, turn them on here.
		toggleUpdatesWhenLocationChanges(updateWhenLocationChanges);
	}

	/**
	 * Find the last known location (using a {@link LastLocationFinder}) and
	 * updates the place list accordingly.
	 * 
	 * @param updateWhenLocationChanges
	 *            Request location updates
	 */
	protected void getLocationAndUpdateTracksNearby(
			boolean updateWhenLocationChanges) {
		// This isn't directly affecting the UI, so put it on a worker thread.
		AsyncTask<Void, Void, Void> findLastLocationTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// Find the last known location, specifying a required accuracy
				// of within the min distance between updates
				// and a required latency of the minimum time required between
				// updates.
				Location lastKnownLocation = lastLocationFinder
						.getLastBestLocation(TuneramblrConstants.MAX_DISTANCE,
								System.currentTimeMillis()
										- TuneramblrConstants.MAX_TIME);

				// TODO: implement the feature to display nearby tracks
				updateTracksNearby(lastKnownLocation,
						TuneramblrConstants.DEFAULT_RADIUS, false);
				return null;
			}
		};
		findLastLocationTask.execute();

		// If we have requested location updates, turn them on here.
		toggleUpdatesWhenLocationChanges(updateWhenLocationChanges);
	}

	/**
	 * Choose if we should receive location updates.
	 * 
	 * @param updateWhenLocationChanges
	 *            Request location updates
	 */
	protected void toggleUpdatesWhenLocationChanges(
			boolean updateWhenLocationChanges) {
		// Save the location update status in shared preferences
		prefsEditor.putBoolean(
				TuneramblrConstants.SP_KEY_FOLLOW_LOCATION_CHANGES,
				updateWhenLocationChanges);
		sharedPreferenceSaver.savePreferences(prefsEditor, true);

		// Start or stop listening for location changes
		if (updateWhenLocationChanges)
			requestLocationUpdates();
		else
			disableLocationUpdates();
	}

	/**
	 * Start listening for location updates.
	 */
	protected void requestLocationUpdates() {
		// Normal updates while activity is visible.
		locationUpdateRequester.requestLocationUpdates(
				TuneramblrConstants.MAX_TIME, TuneramblrConstants.MAX_DISTANCE,
				criteria, locationListenerPendingIntent);

		// Passive location updates from 3rd party apps when the Activity isn't
		// visible.
		locationUpdateRequester.requestPassiveLocationUpdates(
				TuneramblrConstants.PASSIVE_MAX_TIME,
				TuneramblrConstants.PASSIVE_MAX_DISTANCE,
				locationListenerPassivePendingIntent);

		// Register a receiver that listens for when the provider I'm using has
		// been disabled.
		IntentFilter intentFilter = new IntentFilter(
				TuneramblrConstants.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED);
		registerReceiver(locProviderDisabledReceiver, intentFilter);

		// Register a receiver that listens for when a better provider than I'm
		// using becomes available.
		String bestProvider = locationManager.getBestProvider(criteria, false);
		String bestAvailableProvider = locationManager.getBestProvider(
				criteria, true);
		if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
			locationManager.requestLocationUpdates(bestProvider, 0, 0,
					bestInactiveLocationProviderListener, getMainLooper());
		}
	}

	/**
	 * Stop listening for location updates
	 */
	protected void disableLocationUpdates() {
		unregisterReceiver(locProviderDisabledReceiver);
		locationManager.removeUpdates(locationListenerPendingIntent);
		locationManager.removeUpdates(bestInactiveLocationProviderListener);
		if (isFinishing())
			lastLocationFinder.cancel();
		if (TuneramblrConstants.DISABLE_PASSIVE_LOCATION_WHEN_USER_EXIT
				&& isFinishing())
			locationManager.removeUpdates(locationListenerPassivePendingIntent);
	}

	/**
	 * One-off location listener that receives updates from the
	 * {@link LastLocationFinder}. This is triggered where the last known
	 * location is outside the bounds of our maximum distance and latency.
	 */
	protected LocationListener oneShotLastLocationUpdateListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			// TODO: eventually update the UI to show tracks nearby
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}
	};

	/**
	 * If the best Location Provider (usually GPS) is not available when we
	 * request location updates, this listener will be notified if / when it
	 * becomes available. It calls requestLocationUpdates to re-register the
	 * location listeners using the better Location Provider.
	 */
	protected LocationListener bestInactiveLocationProviderListener = new LocationListener() {
		public void onLocationChanged(Location l) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
			// Re-register the location listeners using the better Location
			// Provider.
			requestLocationUpdates();
		}
	};

	/**
	 * If the Location Provider we're using to receive location updates is
	 * disabled while the app is running, this Receiver will be notified,
	 * allowing us to re-register our Location Receivers using the best
	 * available Location Provider is still available.
	 */
	protected BroadcastReceiver locProviderDisabledReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean providerDisabled = !intent.getBooleanExtra(
					LocationManager.KEY_PROVIDER_ENABLED, false);
			// Re-register the location listeners using the best available
			// Location Provider.
			if (providerDisabled)
				requestLocationUpdates();
		}
	};

	/**
	 * Request that the {@link CheckinFragment} UI be updated with the details
	 * corresponding to the specified ID.
	 * 
	 * @param id
	 *            Place Identifier
	 */
	public void updateCheckinFragment(String id) {
		if (id != null) {
			// TODO: update the checkin fragment
		}
	}

	/**
	 * Update the list of nearby places centered on the specified Location,
	 * within the specified radius. This will start the
	 * {@link PlacesUpdateService} that will poll the underlying web service.
	 * 
	 * @param location
	 *            Location
	 * @param radius
	 *            Radius (meters)
	 * @param forceRefresh
	 *            Force Refresh
	 */
	protected void updateTracksNearby(Location location, int radius,
			boolean forceRefresh) {
		if (location != null) {
			Log.d(TAG, "Updating nearby track list.");
			// TODO: implement logic to update the local track listing
		} else
			Log.d(TAG, "Updating nearby track list: No Previous Location Found");
	}

	/**
	 * Receiver that listens for checkins when the Activity is visible. It
	 * should update the Checkin Fragment with the details for the last track
	 * submitted
	 */
	protected BroadcastReceiver checkinReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO: do something
		}
	};

	/**
	 * Receiver that listens for logins when the Activity is visible.
	 */
	protected BroadcastReceiver loginReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// remove the login fragment
			getSupportFragmentManager().beginTransaction()
					.remove(loginFragment).commit();

			// add the checkin fragment
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, checkinFragment).commit();
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// the image should have been taken and saved, just retrieve the URI
		if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
			// TODO: if something bad happens interrupt the application, i
			// guess...
		}
	}

}