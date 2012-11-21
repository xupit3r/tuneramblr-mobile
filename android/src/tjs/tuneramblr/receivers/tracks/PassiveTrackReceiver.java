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
import tjs.tuneramblr.meta.location.base.ILastLocationFinder;
import tjs.tuneramblr.meta.location.utils.PlatformSpecificImplementationFactory;
import tjs.tuneramblr.meta.model.CheckinType;
import tjs.tuneramblr.meta.model.TrackInfo;
import tjs.tuneramblr.services.TrackCheckinService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Encapsulates the shared functionality amongst the track broadcast receivers
 */
public abstract class PassiveTrackReceiver extends BroadcastReceiver {

	/*
	 * helpful constants for various commands associated with the play state
	 * change
	 */
	protected static final String CMD_TOGGLEPAUSE = "togglepause";
	protected static final String CMD_STOP = "stop";
	protected static final String CMD_PAUSE = "pause";
	protected static final String CMD_PREVIOUS = "previous";
	protected static final String CMD_NEXT = "next";

	/* helpful constants for accessing members of the track change intent */
	protected static final String EXTRA_COMMAND_KEY = "command";
	protected static final String EXTRA_ARTIST_KEY = "artist";
	protected static final String EXTRA_ALBUM_KEY = "album";
	protected static final String EXTRA_TRACK_KEY = "track";
	protected static final String EXTRA_DURATION_KEY = "duration";
	protected static final String EXTRA_PLAYING_KEY = "playing";
	protected static final String EXTRA_PLAY_STATE_KEY = "playstate";
	protected static final String EXTRA_POSITION_KEY = "position";

	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = null;

	/**
	 * Prepares intent for and calls checkin service to submit the track
	 * information to the server.
	 * 
	 * @param context
	 *            the current application context
	 * @param trackInfo
	 *            the basic track information (artist, track name, etc.)
	 * @param checkinType
	 *            the type of checkin
	 */
	protected void submitTrack(Context context, TrackInfo trackInfo,
			CheckinType checkinType) {
		ILastLocationFinder locationFinder = PlatformSpecificImplementationFactory
				.getLastLocationFinder(context);
		Intent trackCheckinIntent = new Intent(context,
				TrackCheckinService.class);
		trackCheckinIntent.putExtra(TuneramblrConstants.EXTRA_USEDEF_KEY,
				EMPTY_STRING);
		trackCheckinIntent.putExtra(TuneramblrConstants.EXTRA_IMG_URI_KEY,
				NULL_STRING);
		trackCheckinIntent.putExtra(TuneramblrConstants.EXTRA_LOCATION_KEY,
				locationFinder.getLastBestLocation(
						TuneramblrConstants.MAX_DISTANCE,
						TuneramblrConstants.MAX_TIME));
		trackCheckinIntent.putExtra(
				TuneramblrConstants.EXTRA_TRACK_CHECKIN_TYPE_KEY, checkinType);
		trackCheckinIntent.putExtra(TuneramblrConstants.EXTRA_TRACK_INFO_KEY,
				trackInfo);
		context.startService(trackCheckinIntent);
	}

	/**
	 * Indicates user's preference for recording track information passively (in
	 * the background).
	 * 
	 * @param context
	 *            the current application context
	 * @return true of the user indicated that track information can be
	 *         collected passively, false otherwise
	 */
	protected boolean isPassivelyRecordTrackInfo(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(
				TuneramblrConstants.PASSIVELY_COLLECT_TRACK_INFO_KEY, false);
	}

	/**
	 * Builds a TrackInfo object from some supplied intent.
	 * 
	 * @param intent
	 *            the intent containing track information
	 * @return a TrackInfo object built from the supplied intent
	 */
	protected TrackInfo pullTrackInfoFromIntent(Intent intent) {
		String artist = intent.getStringExtra(EXTRA_ARTIST_KEY);
		String album = intent.getStringExtra(EXTRA_ALBUM_KEY);
		String track = intent.getStringExtra(EXTRA_TRACK_KEY);

		return new TrackInfo(artist, album, track);
	}

	/**
	 * Pulls the command associated with a given intent.
	 * 
	 * @param intent
	 *            the intent from which the command should be pulled
	 * @return the command associated with the supplied intent
	 */
	protected String pullCommandFromIntent(Intent intent) {
		return intent.getStringExtra(EXTRA_COMMAND_KEY);
	}

	/**
	 * Pulls the duration from the given intent.
	 * 
	 * @param intent
	 *            the intent from which the duration should be pulled
	 * @return the duration of the track in milliseconds
	 */
	protected long pullDurationFromIntent(Intent intent) {
		return intent.getLongExtra(EXTRA_DURATION_KEY, 0L);
	}

	/**
	 * Pulls the playing value from the supplied intent
	 * 
	 * @param intent
	 *            the intent from which the playing value should be pulled
	 * @return the playing state of the current track
	 */
	protected boolean pullPlayingFromIntent(Intent intent) {
		return intent.getBooleanExtra(EXTRA_PLAYING_KEY, false);
	}

	/**
	 * Pulls the playstate from the provided intent
	 * 
	 * @param intent
	 *            the intent from which the playstate should be pulled
	 * @return the current play state of the track (false if the track is
	 *         paused, true otherwise)
	 */
	protected boolean pullPlayStateFromIntent(Intent intent) {
		return intent.getBooleanExtra(EXTRA_PLAY_STATE_KEY, false);
	}

	/**
	 * Pulls the current position (in time) in the track.
	 * 
	 * @param intent
	 *            the intent from which the track's time position should be
	 *            pulled
	 * @return the current position (in time) of the track
	 */
	protected long pullPositionFromIntent(Intent intent) {
		return intent.getLongExtra(EXTRA_POSITION_KEY, 0L);
	}

	/**
	 * Pulls the action associated with this intent.
	 * 
	 * @param intent
	 *            the intent from which the action should be pulled
	 * @return the action associated with this intent
	 */
	protected String pullActionFromIntent(Intent intent) {
		return intent.getAction();
	}
}
