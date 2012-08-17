package tjs.tuneramblr.receivers.tracks;

import tjs.tuneramblr.meta.model.CheckinType;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class PlayStateChangedReceiver extends PassiveTrackReceiver {

	private static final String TAG = "PlayStateChangedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "received some track info: "
				+ pullTrackInfoFromIntent(intent));

		String command = pullCommandFromIntent(intent);
		if (CMD_NEXT.equals(command)) {
			// we skipped this track, record it!
			submitTrack(context, pullTrackInfoFromIntent(intent),
					CheckinType.SKIP);
		}

		// TODO: add support for repeat

	}

	/**
	 * builds and returns an intent filter for the play state changed intent
	 * 
	 * @return an intent filter for play state changed.
	 */
	public static IntentFilter buildPlayStateChangedFilter() {
		IntentFilter iF = new IntentFilter();

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
