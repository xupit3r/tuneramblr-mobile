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
package tjs.tuneramblr.receivers.tracks;

import tjs.tuneramblr.meta.model.CheckinType;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MetaChangedReceiver extends PassiveTrackReceiver {

	private static final String TAG = "MetaChangedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Received some track info: "
				+ pullTrackInfoFromIntent(intent));

		String command = pullCommandFromIntent(intent);
		for (String key : intent.getExtras().keySet()) {
			Log.i(TAG, key + " = " + intent.getExtras().get(key));
		}
		Log.i(TAG, "Command: " + command);
		if (CMD_NEXT.equals(command)) {
			// we skipped this track, record it!
			submitTrack(context, pullTrackInfoFromIntent(intent),
					CheckinType.SKIP);
		}
	}

	/**
	 * Builds and returns an intent filter for the meta data changed intent
	 * 
	 * @return an intent filter for the meta data changed intent
	 */
	public static IntentFilter buildMetaChangedFilter() {
		IntentFilter iF = new IntentFilter();

		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.miui.player.metachanged");
		iF.addAction("com.htc.music.metachanged");
		iF.addAction("com.nullsoft.winamp.metachanged");
		iF.addAction("com.real.IMP.metachanged");
		iF.addAction("com.amazon.mp3.metachanged");
		iF.addAction("com.rdio.android.metachanged");

		return iF;
	}

}
