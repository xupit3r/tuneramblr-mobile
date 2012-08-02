package tjs.tuneramblr.receivers.tracks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PlaybackCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String currentArtist = intent.getStringExtra("artist");
		String currentAlbum = intent.getStringExtra("album");
		String currentTrack = intent.getStringExtra("track");

		// DO SOME AWESOME STUFF HERE!
	}

	/**
	 * Builds and returns an intent filter for the playback complete intent
	 * 
	 * @return an intent filter for the playback complete intent
	 */
	public static IntentFilter buildPlayBackCompleteFilter() {
		IntentFilter iF = new IntentFilter();

		// stock music player and google music
		iF.addAction("com.android.music.playbackcomplete");

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
