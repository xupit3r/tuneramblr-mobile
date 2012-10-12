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

import tjs.tuneramblr.TuneramblrConstants;
import tjs.tuneramblr.meta.model.CheckinType;
import tjs.tuneramblr.meta.model.TrackInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Receives and interprets track metadata
 */
public class MediaInfoReceiver extends PassiveTrackReceiver {

	private static final String BLANK_ARTIST = "blank_artist";
	private static final String BLANK_ALBUM = "blank_album";
	private static final String BLANK_TITLE = "blank_title";

	private static final String PLAY_STATE_CHANGED_ACTION = "playstatechanged";

	private static TrackInfo lastTrackInfo = new TrackInfo(BLANK_ARTIST,
			BLANK_ALBUM, BLANK_TITLE);
	private static long lastTrackDuration = 1L;
	private static long lastTrackPosition = 0L;
	private static long lastTrackTimestamp = 1L;

	@Override
	public void onReceive(Context context, Intent intent) {
		long currentTrackDuration = pullDurationFromIntent(intent);
		long currentTrackPosition = pullPositionFromIntent(intent);
		TrackInfo currentTrackInfo = pullTrackInfoFromIntent(intent);
		String intentAction = pullActionFromIntent(intent);
		String genericAction = getGenericAction(intentAction);

		if (PLAY_STATE_CHANGED_ACTION.equals(genericAction)) {
			boolean paused = !pullPlayStateFromIntent(intent);
			if (!paused) {
				lastTrackPosition = currentTrackPosition;
				lastTrackTimestamp = System.currentTimeMillis();
			}
		} else if (currentTrackPosition < 0) {
			/*
			 * a track's position will only be -1 when the metadata changes and
			 * a new track is beginning. either the track was skipped or it was
			 * completed. if the track was skipped, record it as such.
			 */
			long elapsedTime = System.currentTimeMillis() - lastTrackTimestamp;
			if (wasTrackSkipped(lastTrackPosition, lastTrackDuration,
					elapsedTime)) {
				submitTrack(context, lastTrackInfo, CheckinType.SKIP);
			}

			/* only update this if the track information has changed */
			lastTrackInfo = currentTrackInfo;
			lastTrackDuration = currentTrackDuration;
			lastTrackPosition = 0;
			lastTrackTimestamp = System.currentTimeMillis();
		}

	}

	/*
	 * determine if a track has been skipped.
	 * 
	 * @return true if the track was skipped, false otherwise
	 */
	private boolean wasTrackSkipped(long currentPosition, long duration,
			long elapsedTime) {
		double percentComplete = (double) (currentPosition + elapsedTime)
				/ (double) duration;
		return percentComplete < TuneramblrConstants.COMPLETED_TRACK_PERCENTAGE;
	}

	/*
	 * Retrieves the generic action associated with an intent update (this is
	 * the last token in the full action name)
	 */
	private String getGenericAction(String action) {
		String[] tokens = action.split("\\.");
		return tokens[tokens.length - 1];
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

		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.miui.player.playstatechanged");
		iF.addAction("com.htc.music.playstatechanged");
		iF.addAction("com.nullsoft.winamp.playstatechanged");
		iF.addAction("com.real.IMP.playstatechanged");
		iF.addAction("com.amazon.mp3.playstatechanged");
		iF.addAction("com.rdio.android.playstatechanged");

		return iF;
	}

}
