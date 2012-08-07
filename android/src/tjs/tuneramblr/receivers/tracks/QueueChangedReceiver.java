package tjs.tuneramblr.receivers.tracks;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class QueueChangedReceiver extends PassiveTrackReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String currentArtist = intent.getStringExtra("artist");
		String currentAlbum = intent.getStringExtra("album");
		String currentTrack = intent.getStringExtra("track");

		// DO SOME AWESOME STUFF HERE!
	}

	/**
	 * Builds and returns an intent filter for the queue changed intent.
	 * 
	 * @return an intent filter for the queue changed intent
	 */
	public static IntentFilter buildQueueChangedFilter() {
		IntentFilter iF = new IntentFilter();

		// stock music player and google music
		iF.addAction("com.android.music.queuechanged");

		// MIUI music player
		iF.addAction("com.miui.player.queuechanged");

		// HTC music player
		iF.addAction("com.htc.music.queuechanged");

		// WinAmp music player
		iF.addAction("com.nullsoft.winamp.queuechanged");

		// MyTouch4G stock music player
		iF.addAction("com.real.IMP.queuechanged");

		return iF;
	}

}
