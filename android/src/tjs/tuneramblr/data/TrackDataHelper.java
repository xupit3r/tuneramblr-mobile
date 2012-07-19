package tjs.tuneramblr.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrackDataHelper extends SQLiteOpenHelper {

	// column names
	protected static final String TRACK_INFO_TABLE_NAME = "track_info";
	protected static final String TRACK_NAME_COL = "track_name";
	protected static final String ALBUM_NAME_COL = "album_name";
	protected static final String ARTIST_NAME_COL = "artist_name";
	protected static final String TIMESTAMP_COL = "timestamp";

	// database and table creation information
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "trackdata.db";
	private static final String TRACK_INFO_TABLE_CREATE = "CREATE TABLE "
			+ TRACK_INFO_TABLE_NAME + " (" + TRACK_NAME_COL + " TEXT, "
			+ ARTIST_NAME_COL + " TEXT, " + ALBUM_NAME_COL + " TEXT, "
			+ TIMESTAMP_COL + " INTEGER);";

	protected TrackDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TRACK_INFO_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

}
