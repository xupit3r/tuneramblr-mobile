package tjs.tuneramblr.receivers.tracks;

import tjs.tuneramblr.meta.model.CheckinType;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class PlaybackCompleteReceiver extends PassiveTrackReceiver {

	private static final String TAG = "PlaybackCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "received some track info: "
				+ pullTrackInfoFromIntent(intent));

		// the whole track was listened to.
		submitTrack(context, pullTrackInfoFromIntent(intent),
				CheckinType.FULLY_LISTENED);
	}

	/**
	 * Builds and returns an intent filter for the playback complete intent
	 * 
	 * @return an intent filter for the playback complete intent
	 */
	public static IntentFilter buildPlayBackCompleteFilter() {
		IntentFilter iF = new IntentFilter();

		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.miui.player.playbackcomplete");
		iF.addAction("com.htc.music.playbackcomplete");
		iF.addAction("com.nullsoft.winamp.playbackcomplete");
		iF.addAction("com.real.IMP.playbackcomplete");
		iF.addAction("com.amazon.mp3.playbackcomplete");
		iF.addAction("com.rdio.android.playbackcomplete");

		return iF;
	}

}
