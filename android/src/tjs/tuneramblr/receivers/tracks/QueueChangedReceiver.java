package tjs.tuneramblr.receivers.tracks;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class QueueChangedReceiver extends PassiveTrackReceiver {

	private static final String TAG = "QueueChangedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Received some track info: "
				+ pullTrackInfoFromIntent(intent));
		for (String key : intent.getExtras().keySet()) {
			Log.i(TAG, key + " = " + intent.getExtras().get(key));
		}
	}

	/**
	 * Builds and returns an intent filter for the queue changed intent.
	 * 
	 * @return an intent filter for the queue changed intent
	 */
	public static IntentFilter buildQueueChangedFilter() {
		IntentFilter iF = new IntentFilter();

		iF.addAction("com.android.music.queuechanged");
		iF.addAction("com.miui.player.queuechanged");
		iF.addAction("com.htc.music.queuechanged");
		iF.addAction("com.nullsoft.winamp.queuechanged");
		iF.addAction("com.real.IMP.queuechanged");
		iF.addAction("com.amazon.mp3.queuechanged");
		iF.addAction("com.rdio.android.queuechanged");

		return iF;
	}

}
