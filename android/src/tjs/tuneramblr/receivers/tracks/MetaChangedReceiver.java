package tjs.tuneramblr.receivers.tracks;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MetaChangedReceiver extends PassiveTrackReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String currentArtist = intent.getStringExtra("artist");
		String currentAlbum = intent.getStringExtra("album");
		String currentTrack = intent.getStringExtra("track");

		// DO SOME AWESOME STUFF HERE!
	}

	/**
	 * Builds and returns an intent filter for the meta data changed intent
	 * 
	 * @return an intent filter for the meta data changed intent
	 */
	public static IntentFilter buildMetaChangedFilter() {
		IntentFilter iF = new IntentFilter();

		// stock music player and Google music
		iF.addAction("com.android.music.metachanged");

		// MIUI music player
		iF.addAction("com.miui.player.metachanged");

		// HTC music player
		iF.addAction("com.htc.music.metachanged");

		// WinAmp music player
		iF.addAction("com.nullsoft.winamp.metachanged");

		// MyTouch4G stock music player
		iF.addAction("com.real.IMP.metachanged");

		return iF;
	}

}
