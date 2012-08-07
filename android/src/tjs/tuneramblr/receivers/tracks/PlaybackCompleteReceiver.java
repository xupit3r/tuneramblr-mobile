package tjs.tuneramblr.receivers.tracks;

import tjs.tuneramblr.meta.model.CheckinType;
import tjs.tuneramblr.meta.model.TrackInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PlaybackCompleteReceiver extends PassiveTrackReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String currentArtist = intent.getStringExtra("artist");
		String currentAlbum = intent.getStringExtra("album");
		String currentTrack = intent.getStringExtra("track");

		// the whole track was listened to.
		submitTrack(context, new TrackInfo(currentArtist, currentAlbum,
				currentTrack), CheckinType.FULLY_LISTENED);
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
		iF.addAction("com.miui.player.playbackcomplete");

		// HTC music player
		iF.addAction("com.htc.music.playbackcomplete");

		// WinAmp music player
		iF.addAction("com.nullsoft.winamp.playbackcomplete");

		// MyTouch4G stock music player
		iF.addAction("com.real.IMP.playbackcomplete");

		return iF;
	}

}
