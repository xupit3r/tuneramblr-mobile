package tjs.tuneramblr.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.client.utils.URIUtils;

import tjs.tuneramblr.meta.model.CheckinType;
import tjs.tuneramblr.meta.model.Weather;
import android.location.Location;

/**
 * URL helper
 */
public class TuneramblrUrlHelper {

	private static final String PROTOCOL_HTTP = "http";
	// private static final String PROTOCOL_HTTP_SSL = "https";

	private static final Integer NO_PORT = -1;
	// private static final Integer DEFAULT_HTTP = 80;

	private static final String QUERY_SEP = "&";

	/* the base songmap URL */
	private static final String TUNERAMBLR_BASE_URL = "tuneramblr.herokuapp.com";

	/* some other important locations (relative) */
	private static final String TUNERAMBLR_ADD_SONG_URL = "/mobile/songs/add";
	private static final String TUNERAMBLR_LOGIN_URL = "/mobile/login";

	private static TuneramblrUrlHelper URL_HELPER = null;

	private TuneramblrUrlHelper() {
		// nadda
	}

	/**
	 * retrieves the lone instance of the TuneramblrUrlHelper
	 * 
	 * @return the lone instance of this class
	 */
	public static TuneramblrUrlHelper getInstance() {
		if (URL_HELPER == null) {
			URL_HELPER = new TuneramblrUrlHelper();
		}
		return URL_HELPER;
	}

	/**
	 * builds the stem of the add song URL. this includes the protocol, port,
	 * base, and location. note: this URL does NOT contain a query string.
	 * 
	 * @return returns a string representing tuneramblr add song URL
	 */
	public URI getAddSongHttpPostUrl() {
		URI builtUrl = null;
		try {
			builtUrl = URIUtils.createURI(PROTOCOL_HTTP, TUNERAMBLR_BASE_URL,
					NO_PORT, TUNERAMBLR_ADD_SONG_URL, null, null);
		} catch (URISyntaxException e) {
			builtUrl = null;
		}

		return builtUrl;
	}

	/**
	 * builds the mobile login URL.this includes the protocol, port, base, and
	 * location. note: this URL does NOT contain a query string.
	 * 
	 * @return returns a string representing tuneramblr mobile login URL
	 */
	public URI getLoginHttpPostUrl() {
		URI builtUrl = null;
		try {
			builtUrl = URIUtils.createURI(PROTOCOL_HTTP, TUNERAMBLR_BASE_URL,
					NO_PORT, TUNERAMBLR_LOGIN_URL, null, null);
		} catch (URISyntaxException e) {
			builtUrl = null;
		}

		return builtUrl;
	}

	/**
	 * Builds an "add" song URI given the set of desired parameters.
	 * 
	 * @param userLocation
	 *            the user's current location (i.e. where the track was
	 *            recorded)
	 * @param trackName
	 *            the name of the track being recorded
	 * @param artist
	 *            the artist of the track
	 * @param album
	 *            the album on which the track appears
	 * @param localWeather
	 *            the weather when the track was recorded
	 * @param userDef
	 *            any user defined tags to be associated with the track
	 * @param username
	 *            the username of the user
	 * @param password
	 *            the password of the user
	 * @param img
	 *            a string representation of the image that is being recorded
	 *            against the track
	 * @param checkinType
	 *            the type of the checking user/passive (skipped track, listened
	 *            to the whole thing, etc.)
	 * @return a valid URI for adding a song with the supplied parameters
	 */
	public URI buildAddSongHttpUrl(Location userLocation, String trackName,
			String artist, String album, Weather localWeather,
			String[] userDef, String username, String password, String img,
			long currentTime, CheckinType checkinType) {
		URI builtUrl = null;

		String queryString = buildAddSongQueryString(userLocation, trackName,
				artist, album, localWeather, userDef, username, password, img,
				currentTime, checkinType);

		try {
			builtUrl = URIUtils.createURI(PROTOCOL_HTTP, TUNERAMBLR_BASE_URL,
					NO_PORT, TUNERAMBLR_ADD_SONG_URL, queryString, null);
		} catch (URISyntaxException e) {
			// this shouldn't happen since I am building it,
			// but if it does, just null out the builtUrl and
			// return that...just need to be sure to check for
			// that on the other side
			builtUrl = null;
		}

		return builtUrl;
	}

	/**
	 * Builds the query string portion of the add song URL
	 * 
	 * @param userLocation
	 *            the user's current location (i.e. where the track was
	 *            recorded)
	 * @param trackName
	 *            the name of the track being recorded
	 * @param artist
	 *            the artist of the track
	 * @param album
	 *            the album on which the track appears
	 * @param localWeather
	 *            the weather when the track was recorded
	 * @param userDef
	 *            any user defined tags to be associated with the track
	 * @param username
	 *            the username of the user
	 * @param password
	 *            the password of the user
	 * @param img
	 *            a string representation of the image that is being recorded
	 *            against the track
	 * @param checkinType
	 *            the type of the checking user/passive (skipped track, listened
	 *            to the whole thing, etc.)
	 * @return a query string representing the set of supplied parameters
	 */
	public String buildAddSongQueryString(Location userLocation,
			String trackName, String artist, String album,
			Weather localWeather, String[] userDef, String username,
			String password, String img, long currentTime,
			CheckinType checkinType) {

		// time to build the query string
		StringBuilder queryBuilder = new StringBuilder();

		boolean last = true;
		addQueryPair(queryBuilder, "lat",
				String.valueOf(userLocation.getLatitude()), !last);
		addQueryPair(queryBuilder, "lng",
				String.valueOf(userLocation.getLongitude()), !last);
		addQueryPair(queryBuilder, "title", trackName, !last);
		addQueryPair(queryBuilder, "artist", artist, !last);
		addQueryPair(queryBuilder, "album", album, !last);
		addQueryPair(queryBuilder, "weather", localWeather.toString(), !last);

		addQueryPair(queryBuilder, "userdef", buildUserDefParam(userDef), !last);

		addQueryPair(queryBuilder, "username", username, !last);
		addQueryPair(queryBuilder, "password", password, !last);

		addQueryPair(queryBuilder, "img", img, !last);
		addQueryPair(queryBuilder, "tstamp", String.valueOf(currentTime), !last);
		addQueryPair(queryBuilder, "ctype", checkinType.toValue(), last);

		return queryBuilder.toString();
	}

	/**
	 * Builds a query string for the login request
	 * 
	 * @param username
	 *            the username of the user
	 * @param password
	 *            the password of the user
	 * @return
	 */
	public String buildLoginQueryString(String username, String password) {
		StringBuilder queryBuilder = new StringBuilder();
		boolean last = true;

		addQueryPair(queryBuilder, "username", username, !last);
		addQueryPair(queryBuilder, "password", password, last);

		return queryBuilder.toString();
	}

	/*
	 * GENERAL HELPER FUNCTIONS
	 */
	private void addQueryPair(StringBuilder builder, String key, String value,
			boolean last) {
		// only add the parameter if the value is NOT null. a null value means
		// we could not get the parameter (if it is an automatically acquired
		// parameter) or the user did not enter it, if it is optional. either
		// way, we do not want to include it in the query string.
		if (value != null) {
			builder.append(key);
			builder.append("=");
			builder.append(URLEncoder.encode(value));
			if (!last) {
				builder.append(QUERY_SEP);
			}
		}

	}

	private String buildUserDefParam(String[] userDef) {
		String userDefStr = "";
		int idx = 0;
		int lastIdx = userDef.length - 1;
		for (String val : userDef) {
			userDefStr += (idx < lastIdx) ? val + "," : val;
			idx++;
		}
		return userDefStr;
	}
}
