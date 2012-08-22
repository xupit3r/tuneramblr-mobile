package tjs.tuneramblr.receivers.tracks;

import tjs.tuneramblr.TuneramblrConstants;
import tjs.tuneramblr.meta.model.CheckinType;
import tjs.tuneramblr.meta.model.TrackInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MetaChangedReceiver extends PassiveTrackReceiver {

	private static final String TAG = "MetaChangedReceiver";

	private static final String BLANK_ARTIST = "blank_artist";
	private static final String BLANK_ALBUM = "blank_album";
	private static final String BLANK_TITLE = "blank_title";

	private static TrackInfo lastTrackInfo = new TrackInfo(BLANK_ARTIST,
			BLANK_ALBUM, BLANK_TITLE);
	private static long lastTrackDuration = 1L;
	private static long lastTrackTimestamp = 1L;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Received some track info: "
				+ pullTrackInfoFromIntent(intent));

		long currentTimestamp = System.currentTimeMillis();
		long currentTrackDuration = pullDurationFromIntent(intent);
		TrackInfo currentTrackInfo = pullTrackInfoFromIntent(intent);

		if (wasTrackSkipped(currentTimestamp)) {
			// we skipped this track, record it!
			submitTrack(context, lastTrackInfo, CheckinType.SKIP);
		}

		lastTrackInfo = currentTrackInfo;
		lastTrackDuration = currentTrackDuration;
		lastTrackTimestamp = currentTimestamp;

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
