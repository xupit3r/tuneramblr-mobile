/*
 * Copyright 2012 Joe D'Alessandro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tjs.tuneramblr;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import tjs.tuneramblr.data.TrackInfoDS;
import tjs.tuneramblr.meta.MetadataManager;
import tjs.tuneramblr.meta.location.base.ILastLocationFinder;
import tjs.tuneramblr.meta.location.utils.PlatformSpecificImplementationFactory;
import tjs.tuneramblr.meta.model.CheckinType;
import tjs.tuneramblr.meta.model.TrackInfo;
import tjs.tuneramblr.meta.music.MetaMediaRequester;
import tjs.tuneramblr.services.TrackCheckinService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * UI Fragment to display the functions necessary to "checkin" a track that we
 * are currently listening to.
 * 
 * Portions of the code that follows was adapted from code samples found at:
 * http://blog.radioactiveyak.com/2011/06/deep-dive-into-location-part-2-being
 * .html
 */
public class CheckinFragment extends Fragment {

	// the URI of the image that can be taken
	protected Uri imageUri;

	protected Handler handler = new Handler();
	protected Activity activity;

	// some UI elements
	protected Button addSongButton;
	protected Button takePhotoButton;
	protected TextView trackNameText;
	protected TextView artistNameText;
	protected TextView albumNameText;

	public CheckinFragment() {
		super();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.checkin, container, false);

		// get a handle on the UI elements
		addSongButton = (Button) view.findViewById(R.id.addButton);
		takePhotoButton = (Button) view.findViewById(R.id.takePhotoBtn);
		trackNameText = (TextView) view.findViewById(R.id.trackNameText);
		artistNameText = (TextView) view.findViewById(R.id.artistNameText);
		albumNameText = (TextView) view.findViewById(R.id.albumNameText);

		// update the track display
		populateTrackDisplay(getActivity().getApplicationContext());

		// listeners
		addSongButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				ILastLocationFinder locationFinder = PlatformSpecificImplementationFactory
						.getLastLocationFinder(v.getContext());

				// this is the only thing coming from the UI at the moment
				// we will be grabbing the rest of the stuff automatically
				// FIXME: what do I want to do about this feature?
				String userDefString = "";

				TrackInfoDS tids = new TrackInfoDS(v.getContext());

				// build an Intent to send over to the checkin service
				Intent trackCheckinIntent = new Intent(v.getContext(),
						TrackCheckinService.class);
				trackCheckinIntent.putExtra(
						TuneramblrConstants.EXTRA_USEDEF_KEY, userDefString);
				trackCheckinIntent.putExtra(
						TuneramblrConstants.EXTRA_IMG_URI_KEY, imageUri);
				trackCheckinIntent.putExtra(
						TuneramblrConstants.EXTRA_LOCATION_KEY, locationFinder
								.getLastBestLocation(
										TuneramblrConstants.MAX_DISTANCE,
										TuneramblrConstants.MAX_TIME));
				trackCheckinIntent.putExtra(
						TuneramblrConstants.EXTRA_TRACK_CHECKIN_TYPE_KEY,
						CheckinType.USER_LIKE);

				tids.open();
				trackCheckinIntent.putExtra(
						TuneramblrConstants.EXTRA_TRACK_INFO_KEY,
						tids.getLastRecordedTrack());
				tids.close();

				v.getContext().startService(trackCheckinIntent);

				// notify the user that the track has been sent
				Toast songResultText = Toast.makeText(v.getContext(),
						R.string.songSubmitted, Toast.LENGTH_LONG);
				songResultText.show();

				// clear the image URI
				imageUri = null;
			}
		});

		takePhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// use the stock camera and save the image to some location on
				// the phone/SDCARD
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				imageUri = getImageUri();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,
						TuneramblrMobileActivity.TAKE_PHOTO_CODE);
			}

			/** Create a File for saving an image */
			public File buildOutputMediaFile() {

				File mediaStorageDir = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						"tuneramblr");

				// Create the storage directory if it does not exist
				if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						Log.d("tuneramblr", "failed to create directory");
						return null;
					}
				}

				// Create a media file name
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				File mediaFile;
				mediaFile = new File(mediaStorageDir.getPath(), "IMG_TR_"
						+ timeStamp + ".jpg");

				return mediaFile;
			}

			/**
			 * Get the uri of the captured file
			 * 
			 * @return A Uri which path is the path of an image file, stored on
			 *         the dcim folder
			 */
			public Uri getImageUri() {
				// Store image in dcim
				File file = buildOutputMediaFile();
				Uri imu = Uri.fromFile(file);

				return imu;
			}
		});

		// this is just to update the UI as new track information is received.
		// this uses the meta media intent filter.
		getActivity().registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				// pull the track info from the intent
				String track = intent.getStringExtra("track");
				String artist = intent.getStringExtra("artist");
				String album = intent.getStringExtra("album");

				// update the UI elements
				trackNameText.setText(track);
				artistNameText.setText(artist);
				albumNameText.setText(album);

			}
		}, MetaMediaRequester.buildMediaIntentFilter());

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		populateTrackDisplay(getActivity().getApplicationContext());
	}

	/**
	 * {@inheritDoc} When this load has finished, update the UI with the name of
	 * the nearby tracks.
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO: update UI as necessary
	}

	/**
	 * {@inheritDoc}
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO: update UI as necessary
	}

	/**
	 * populates the track information display
	 */
	private void populateTrackDisplay(Context context) {
		// set the track info text views
		MetadataManager metadataManger = new MetadataManager();
		TrackInfo trackInfo = metadataManger.getTrackInfo(context);
		trackNameText.setText(trackInfo.getTrack());
		artistNameText.setText(trackInfo.getArtist());
		albumNameText.setText(trackInfo.getAlbum());
	}
}