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

package tjs.tuneramblr.services;

import tjs.tuneramblr.R;
import tjs.tuneramblr.TuneramblrConstants;
import tjs.tuneramblr.TuneramblrMobileActivity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

/**
 * Service to provide notification of the result of the song addition.
 */
public class TrackCheckinNotificationService extends IntentService {

	protected static String TAG = "TrackCheckinNotificationService";

	protected ContentResolver contentResolver;
	protected NotificationManager notificationManager;
	protected String[] projection;

	public TrackCheckinNotificationService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		contentResolver = getContentResolver();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// pull the message from the intent
		String checkinMessage = intent
				.getStringExtra(TuneramblrConstants.EXTRA_CHECKIN_NOTIFICATION_KEY);

		// build a notification
		Notification notification = new Notification(R.drawable.tr_logo,
				checkinMessage, System.currentTimeMillis());

		// setup required notification information
		// TODO: tailor this a bit more
		Context context = getApplicationContext();
		CharSequence contentTitle = checkinMessage;
		CharSequence contentText = checkinMessage;
		Intent notificationIntent = new Intent(this,
				TuneramblrMobileActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		// display the notification
		notificationManager.notify(TuneramblrConstants.CHECKIN_NOTIFICATION,
				notification);
	}
}