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
package tjs.tuneramblr.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * class to aid in the creation of the user_track_info table
 */
public class UserTrackDataHelper extends SQLiteOpenHelper {

	// column names
	protected static final String USER_TRACK_INFO_TABLE_NAME = "user_track_info";
	protected static final String USER_TRACK_ID_COL = "track_id";
	protected static final String USER_TRACK_TIMESTAMP_COL = "track_ts";
	protected static final String USER_TRACK_BLOB_COL = "track_blob";

	// database and table creation information
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "usertrackdata.db";
	private static final String USER_TRACK_INFO_TABLE_CREATE = "CREATE TABLE "
			+ USER_TRACK_INFO_TABLE_NAME + " (" + USER_TRACK_ID_COL
			+ " INTEGER, " + USER_TRACK_TIMESTAMP_COL + " INTEGER, "
			+ USER_TRACK_BLOB_COL + " BLOB);";

	protected UserTrackDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(USER_TRACK_INFO_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

}
