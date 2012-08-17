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
				TuneramblrConstants.EXTRA_TRACK_CHECKIN_TYPE_KEY,
				CheckinType.USER_LIKE);
		trackCheckinIntent.putExtra(TuneramblrConstants.EXTRA_TRACK_INFO_KEY,
				trackInfo);
		context.startService(trackCheckinIntent);
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
}
