package tjs.tuneramblr.meta.music;

import tjs.tuneramblr.data.TrackInfoDS;
import tjs.tuneramblr.meta.model.TrackInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * receives track updates and stores them in the application's local storage.
 */
public class MetaMediaRequester extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String currentArtist = intent.getStringExtra("artist");
		String currentAlbum = intent.getStringExtra("album");
		String currentTrack = intent.getStringExtra("track");

		TrackInfoDS trackInfoDs = new TrackInfoDS(context);
		trackInfoDs.open();
		trackInfoDs.addTrackInfo(new TrackInfo(currentArtist, currentAlbum,
				currentTrack));
		trackInfoDs.close();
	}

	/*
	 * prepares an intent filter for handling broadcasts related to media
	 * metadata
	 */
	public static IntentFilter buildMediaIntentFilter() {
		IntentFilter iF = new IntentFilter();

		// stock music player and google music
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");

		// MIUI music player
		iF.addAction("com.miui.player.metachanged");
		iF.addAction("com.miui.player.playstatechanged");
		iF.addAction("com.miui.player.playbackcomplete");
		iF.addAction("com.miui.player.queuechanged");

		// HTC music player
		iF.addAction("com.htc.music.metachanged");
		iF.addAction("com.htc.music.playstatechanged");
		iF.addAction("com.htc.music.playbackcomplete");
		iF.addAction("com.htc.music.queuechanged");

		// WinAmp music player
		iF.addAction("com.nullsoft.winamp.metachanged");
		iF.addAction("com.nullsoft.winamp.playstatechanged");
		iF.addAction("com.nullsoft.winamp.playbackcomplete");
		iF.addAction("com.nullsoft.winamp.queuechanged");

		// MyTouch4G stock music player
		iF.addAction("com.real.IMP.metachanged");
		iF.addAction("com.real.IMP.playstatechanged");
		iF.addAction("com.real.IMP.playbackcomplete");
		iF.addAction("com.real.IMP.queuechanged");

		// Amazon MP3
		iF.addAction("com.amazon.mp3.metachanged");
		iF.addAction("com.amazon.mp3.playstatechanged");
		iF.addAction("com.amazon.mp3.playbackcomplete");
		iF.addAction("com.amazon.mp3.queuechanged");

		// rdio
		iF.addAction("com.rdio.android.metachanged");
		iF.addAction("com.rdio.android.playstatechanged");
		iF.addAction("com.rdio.android.playbackcomplete");
		iF.addAction("com.rdio.android.queuechanged");

		return iF;
	}
}
