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
}
