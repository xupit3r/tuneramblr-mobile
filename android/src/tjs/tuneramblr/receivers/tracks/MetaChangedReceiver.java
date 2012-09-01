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
public class MetaChangedReceiver extends PassiveTrackReceiver {

	private static final String TAG = "MetaChangedReceiver";

	private static final String BLANK_ARTIST = "blank_artist";
	private static final String BLANK_ALBUM = "blank_album";
	private static final String BLANK_TITLE = "blank_title";

	private static TrackInfo lastTrackInfo = new TrackInfo(BLANK_ARTIST,
			BLANK_ALBUM, BLANK_TITLE);
	private static long lastTrackDuration = 1L;
	private static long lastTrackTimestamp = 1L;
	private static long trackPausedTimestamp = 1L;
	private static boolean wasPaused = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		long currentTrackTimestamp = System.currentTimeMillis();
		long currentTrackDuration = pullDurationFromIntent(intent);
		TrackInfo currentTrackInfo = pullTrackInfoFromIntent(intent);
		boolean playing = pullPlayingFromIntent(intent);

		if (wasTrackPaused(currentTrackInfo, lastTrackInfo, playing)) {
			// the track was paused
			trackPausedTimestamp = System.currentTimeMillis();
			wasPaused = true;
		} else if (wasPaused && currentTrackInfo.equals(lastTrackInfo)) {
			// the track was resumed
			currentTrackTimestamp = System.currentTimeMillis()
					- (trackPausedTimestamp - lastTrackTimestamp);
			wasPaused = false;
		} else if (!currentTrackInfo.equals(lastTrackInfo)) {
			long currentTime = System.currentTimeMillis();
			if (wasPaused) {
				// if we were previously paused, we need to adjust the timestamp
				// of the track to resemble the correct elapsed time
				lastTrackTimestamp = getShiftedTrackTimestamp(currentTime,
						trackPausedTimestamp, lastTrackTimestamp);
			}

			if (wasTrackSkipped(currentTime)) {
				// we skipped this track, record it!
				submitTrack(context, lastTrackInfo, CheckinType.SKIP);
			}
		}

		lastTrackInfo = currentTrackInfo;
		lastTrackDuration = currentTrackDuration;
		lastTrackTimestamp = currentTrackTimestamp;

	}

	/*
	 * shifts a track timestamp to the appropriate time upon resume
	 * 
	 * @return the time in milliseconds that the track started after the resume
	 * time
	 */
	private long getShiftedTrackTimestamp(long currentTime, long pausedTime,
			long trackTimestamp) {
		return currentTime - (pausedTime - trackTimestamp);
	}

	/*
	 * determine if a track has been paused
	 * 
	 * @return true if the track has been paused, false otherwise
	 */
	private boolean wasTrackPaused(TrackInfo currentTrack, TrackInfo lastTrack,
			boolean playing) {
		return currentTrack.equals(lastTrack) && !playing;
	}

	/*
	 * determine if a track has been skipped
	 * 
	 * @return true if the track was skipped, false otherwise
	 */
	private boolean wasTrackSkipped(long currentTime) {
		double elapsedTime = currentTime - lastTrackTimestamp;
		double percentComplete = (double) elapsedTime
				/ (double) lastTrackDuration;

		return percentComplete < TuneramblrConstants.COMPLETED_TRACK_PERCENTAGE;
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
