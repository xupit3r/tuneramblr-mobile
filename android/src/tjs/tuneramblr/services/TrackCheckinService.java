package tjs.tuneramblr.services;

import java.io.IOException;
import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import tjs.tuneramblr.TuneramblrConstants;
import tjs.tuneramblr.data.UserInfoDS;
import tjs.tuneramblr.meta.MetadataManager;
import tjs.tuneramblr.meta.img.BasicImageRequester;
import tjs.tuneramblr.meta.img.ImageRequester;
import tjs.tuneramblr.meta.location.utils.PlatformSpecificImplementationFactory;
import tjs.tuneramblr.meta.model.TrackInfo;
import tjs.tuneramblr.meta.model.UserInfo;
import tjs.tuneramblr.meta.model.Weather;
import tjs.tuneramblr.util.HttpUtil;
import tjs.tuneramblr.util.TuneramblrUrlHelper;
import tjs.tuneramblr.util.base.SharedPreferenceSaver;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

/**
 * Service to carry out a track checkin. Pulls metadata and passing along the
 * notification of the success or failure.
 */
public class TrackCheckinService extends IntentService {

	private String CHECKIN_UNSUCCESSFUL_MESSAGE = "Oh, NOES! Sumpin bad hapind!";

	protected static String TAG = "TrackCheckinService";

	protected ContentResolver contentResolver;
	protected ConnectivityManager cm;
	protected AlarmManager alarmManager;
	protected PendingIntent retryQueuedCheckinsPendingIntent;
	protected SharedPreferences sharedPreferences;
	protected Editor sharedPreferencesEditor;
	protected SharedPreferenceSaver sharedPreferenceSaver;

	public TrackCheckinService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		contentResolver = getContentResolver();
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		sharedPreferences = getSharedPreferences(
				TuneramblrConstants.SHARED_PREFERENCE_FILE,
				Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferenceSaver = PlatformSpecificImplementationFactory
				.getSharedPreferenceSaver(this);

		Intent retryIntent = new Intent(
				TuneramblrConstants.RETRY_QUEUED_CHECKINS_ACTION);
		retryQueuedCheckinsPendingIntent = PendingIntent.getBroadcast(this, 0,
				retryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	/**
	 * performs a checkin and handles the result
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		CheckinResult result = checkin(intent);

		// now prepare an intent to startup the notification service
		Intent trackCheckinIntent = new Intent(getApplicationContext(),
				TrackCheckinNotificationService.class);
		trackCheckinIntent.putExtra(
				TuneramblrConstants.EXTRA_CHECKIN_NOTIFICATION_KEY,
				result.message);

		// now fire up the notification service
		startService(trackCheckinIntent);
	}

	/**
	 * Performs a checkin for the current track and meta data
	 * 
	 * @param intent
	 *            the intent sent to this service
	 * @return a result indicating success or failure
	 */
	protected CheckinResult checkin(Intent intent) {
		String addSongMessage = "";
		CheckinResult result = null;
		try {

			// necessary?
			String genre = "genre";

			// pull content out of the intents
			String userDefString = intent
					.getStringExtra(TuneramblrConstants.EXTRA_USEDEF_KEY);
			Uri imageUri = intent
					.getParcelableExtra(TuneramblrConstants.EXTRA_IMG_URI_KEY);
			Location userLocation = intent
					.getParcelableExtra(TuneramblrConstants.EXTRA_LOCATION_KEY);

			// I want to also grab the time
			long currentTime = System.currentTimeMillis();

			// this assumes that the user def input is CSV string
			String[] userDef = userDefString.split(",");

			// TODO: store the tags locally for suggestion to the user in the
			// future

			// build a metadata manager
			MetadataManager metaManager = new MetadataManager();

			// pull the track info
			TrackInfo trackInfo = metaManager
					.getTrackInfo(getApplicationContext());

			// don't even bother with the rest of this stuff if
			// we were unable to retrieve the track information
			if (haveTrackInfo(trackInfo)) {

				// we now know that we have some track information, so why don't
				// we go ahead and pull that
				String artist = trackInfo.getArtist();
				String album = trackInfo.getAlbum();
				String trackName = trackInfo.getTrack();

				// username and password
				UserInfoDS userInfoDs = new UserInfoDS(getApplicationContext());
				UserInfo userInfo = userInfoDs.readUserInfo();
				String username = userInfo.getUsername();
				String password = userInfo.getPassword();

				try {
					// get the current weather
					Weather localWeather = metaManager
							.getLocalWeather(userLocation);

					// get the image, well, get it if the user actually took a
					// picture. nothing says that you are required to do so, so
					// if they did not take a picture the imageUri will be null,
					// check for that
					String imgStr = null;
					if (imageUri != null) {
						ImageRequester imgRequester = new BasicImageRequester(
								getContentResolver(), imageUri);
						imgStr = imgRequester.retrieveImage();
					}

					// get the add song URI to POST to
					// TODO: HTTP + SSL
					URI addSongUri = TuneramblrUrlHelper.getInstance()
							.getAddSongHttpPostUrl();

					// something could have gone wrong when we created the URI,
					// which would result in a NULL URI. Let's check this.
					if (addSongUri != null) {
						String queryString = TuneramblrUrlHelper.getInstance()
								.buildAddSongQueryString(userLocation,
										trackName, artist, album, genre,
										localWeather, userDef, username,
										password, imgStr, currentTime);
						String responseString = HttpUtil.getInstance()
								.makePost(addSongUri, queryString);

						// parse the response and pull the response message
						JSONObject songResultJson = new JSONObject(
								responseString);
						addSongMessage = songResultJson.getString("message");
					}

				} catch (IOException ioe) {
					Log.e(TAG,
							"Failed to make call to server: "
									+ ioe.getMessage(), ioe);
				} catch (JSONException je) {
					Log.e(TAG,
							"Failed to make call to server: " + je.getMessage(),
							je);
				}
			} else {
				Log.e(TAG,
						"TrackInfo came back null.  Did not attempt to add the track.");
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException occurred");
		} finally {

			// if we got a response back, show the message
			if (addSongMessage != null) {
				Log.d(TAG, "Track has been added.");
				result = new CheckinResult(addSongMessage, true);
			} else {
				Log.d(TAG, "Track has not been added.");
				result = new CheckinResult(CHECKIN_UNSUCCESSFUL_MESSAGE, false);
				// TODO: add the request to the retry queue
			}
		}
		return result;
	}

	// determines if the supplied track info contains meaningful information
	private boolean haveTrackInfo(TrackInfo ti) {
		return (ti.getAlbum() != null) && (ti.getArtist() != null)
				&& (ti.getTrack() != null);
	}

	/**
	 * Encapsulates data related to the result of a checkin attempt
	 */
	public final class CheckinResult {
		private boolean success = false;
		private String message = "";

		CheckinResult(String message, boolean sucess) {
			this.message = message;
			this.success = sucess;
		}

		String getMessage() {
			return message;
		}

		boolean getSuccess() {
			return success;
		}
	}
}