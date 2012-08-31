/*
 * Copyright 2011 Google Inc.
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

package tjs.tuneramblr.receivers;

import tjs.tuneramblr.TuneramblrConstants;
import tjs.tuneramblr.meta.location.base.LocationRequester;
import tjs.tuneramblr.meta.location.utils.PlatformSpecificImplementationFactory;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;

/**
 * Enables passive location updates after a system reboot.
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = context.getSharedPreferences(
				TuneramblrConstants.SHARED_PREFERENCE_FILE,
				Context.MODE_PRIVATE);
		boolean runOnce = prefs.getBoolean(TuneramblrConstants.SP_KEY_RUN_ONCE,
				false);

		if (runOnce) {
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

			// grab a platform specific location requester. the requester will
			// be based on the constants setup in TuneramblrConstants
			LocationRequester locationRequester = PlatformSpecificImplementationFactory
					.getLocationRequester(locationManager);

			boolean followLocationChanges = prefs.getBoolean(
					TuneramblrConstants.SP_KEY_FOLLOW_LOCATION_CHANGES, true);

			if (followLocationChanges) {
				// retrieve passive location updates from other third-party apps
				// when this app isn't visible
				Intent passiveIntent = new Intent(context,
						PassiveLocationChangedReceiver.class);
				PendingIntent locationListenerPassivePendingIntent = PendingIntent
						.getActivity(context, 0, passiveIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);
			}

		}

	}
}
