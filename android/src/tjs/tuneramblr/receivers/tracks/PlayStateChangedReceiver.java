package tjs.tuneramblr.receivers.tracks;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PlayStateChangedReceiver extends PassiveTrackReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String currentArtist = intent.getStringExtra("artist");
		String currentAlbum = intent.getStringExtra("album");
		String currentTrack = intent.getStringExtra("track");

		// DO SOME AWESOME STUFF HERE!
	}

	/**
	 * builds and returns an intent filter for the play state changed intent
	 * 
	 * @return an intent filter for play state changed.
	 */
	public static IntentFilter buildPlayStateChangedFilter() {
		IntentFilter iF = new IntentFilter();

		// stock music player and google music
		iF.addAction("com.android.music.playstatechanged");

		// MIUI music player
		iF.addAction("com.miui.player.playstatechanged");

		// HTC music player
		iF.addAction("com.htc.music.playstatechanged");

		// WinAmp music player
		iF.addAction("com.nullsoft.winamp.playstatechanged");

		// MyTouch4G stock music player
		iF.addAction("com.real.IMP.playstatechanged");

		return iF;
	}
}
