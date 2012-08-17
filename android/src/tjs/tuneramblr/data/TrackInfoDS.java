package tjs.tuneramblr.data;

import tjs.tuneramblr.meta.model.TrackInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrackInfoDS {

	private final int TRACK_NAME_COL_NUM = 0;
	private final int ARTIST_NAME_COL_NUM = 1;
	private final int ALBUM_NAME_COL_NUM = 2;

	private TrackDataHelper trackDataHelper;
	private SQLiteDatabase db;

	// array of all columns to return (for queries)
	private String[] returnColumns = { TrackDataHelper.TRACK_NAME_COL,
			TrackDataHelper.ARTIST_NAME_COL, TrackDataHelper.ALBUM_NAME_COL };

	public TrackInfoDS(Context context) {
		super();
		trackDataHelper = new TrackDataHelper(context);
	}

	/**
	 * This opens the database for both reading and writing
	 */
	public void open() {
		db = trackDataHelper.getWritableDatabase();
	}

	public void close() {
		trackDataHelper.close();
	}

	/**
	 * Adds the given track information to the application's internal data
	 * storage
	 * 
	 * @param trackInfo
	 */
	public void addTrackInfo(TrackInfo trackInfo) {

		// prepare the values for insert
		ContentValues values = new ContentValues();
		values.put(TrackDataHelper.TRACK_NAME_COL, trackInfo.getTrack());
		values.put(TrackDataHelper.ARTIST_NAME_COL, trackInfo.getArtist());
		values.put(TrackDataHelper.ALBUM_NAME_COL, trackInfo.getAlbum());
		values.put(TrackDataHelper.TIMESTAMP_COL, System.currentTimeMillis());

		// now insert the new track info
		db.insert(TrackDataHelper.TRACK_INFO_TABLE_NAME, null, values);
	}

	/**
	 * pulls the most recently played track from the application's internal data
	 * storage
	 * 
	 * @return the track info for the last recorded track or null if there is
	 *         not a last recorded track
	 */
	public TrackInfo getLastRecordedTrack() {
		Log.i("TrackInfoDS", (db == null) ? "db is null"
				: "what the hell is null?");
		Cursor cursor = db.query(TrackDataHelper.TRACK_INFO_TABLE_NAME,
				returnColumns, null, null, null, null,
				TrackDataHelper.TIMESTAMP_COL + " desc ", "1");

		TrackInfo trackInfo = cursor.moveToFirst() ? cursorToTrackInfo(cursor)
				: null;
		cursor.close();

		return trackInfo;
	}

	private TrackInfo cursorToTrackInfo(Cursor cursor) {
		String trackName = cursor.getString(TRACK_NAME_COL_NUM);
		String artistName = cursor.getString(ARTIST_NAME_COL_NUM);
		String albumName = cursor.getString(ALBUM_NAME_COL_NUM);

		return new TrackInfo(artistName, albumName, trackName);
	}
}
