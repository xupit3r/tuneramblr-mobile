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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import tjs.tuneramblr.meta.model.UserTrackInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Logic for storing and retrieving UserTrackInfo.
 */
public class UserTrackDataDS {

	private static final String TAG = "UserTrackDataDS";

	private final int TRACK_DATA_ID_COL_NUM = 0;
	private final int TRACK_DATA_TIMESTAMP_COL_NUM = 1;
	private final int TRACK_DATA_BLOB_COL_NUM = 2;

	// array of all columns to return (for queries)
	private String[] returnColumns = { UserTrackDataHelper.USER_TRACK_BLOB_COL };

	private UserTrackDataHelper userTrackDataHelper;
	private SQLiteDatabase db;

	/**
	 * Builds a user track data store.
	 * 
	 * @param context
	 *            the current application context
	 */
	public UserTrackDataDS(Context context) {
		super();
		userTrackDataHelper = new UserTrackDataHelper(context);
	}

	/**
	 * This opens the data store for both reading and writing
	 */
	public void open() {
		db = userTrackDataHelper.getWritableDatabase();
	}

	/**
	 * Closes the data store.
	 */
	public void close() {
		userTrackDataHelper.close();
	}

	/**
	 * Adds a UserTrackInfo to the data store
	 * 
	 * @param uti
	 *            the UserTrackInfo to store
	 */
	public void addUserTrackInfo(UserTrackInfo uti) {
		ContentValues values = new ContentValues();
		values.put(UserTrackDataHelper.USER_TRACK_ID_COL, uti.hashCode());
		values.put(UserTrackDataHelper.USER_TRACK_TIMESTAMP_COL,
				System.currentTimeMillis());
		values.put(UserTrackDataHelper.USER_TRACK_BLOB_COL, getDemBytes(uti));

		// now insert the new user track info
		db.insert(UserTrackDataHelper.USER_TRACK_INFO_TABLE_NAME, null, values);
	}

	/**
	 * Retrieves the UserTrackInfo object that has been in the store longest.
	 * 
	 * @return the oldest UserTrackInfo
	 */
	public UserTrackInfo getOldestUserTrackInfo() {
		UserTrackInfo uti = null;
		Cursor cursor = db.query(
				UserTrackDataHelper.USER_TRACK_INFO_TABLE_NAME, returnColumns,
				null, null, null, null,
				UserTrackDataHelper.USER_TRACK_TIMESTAMP_COL + " desc ", "1");

		uti = cursor.moveToFirst() ? cursorToUserTrackInfo(cursor) : null;
		cursor.close();
		return uti;
	}

	/**
	 * Deletes the supplied UserTrackInfo object from the data store
	 * 
	 * @param uti
	 *            the UserTrackInfo object to delete
	 */
	private void removeUserTrackInfo(UserTrackInfo uti) {
		db.delete(UserTrackDataHelper.USER_TRACK_INFO_TABLE_NAME,
				UserTrackDataHelper.USER_TRACK_ID_COL + "=?",
				new String[] { Integer.toString(uti.hashCode()) });
	}

	private UserTrackInfo cursorToUserTrackInfo(Cursor cursor) {
		byte[] userTrackBytes = cursor.getBlob(TRACK_DATA_BLOB_COL_NUM);
		UserTrackInfo userTrackInfo = getDatObject(userTrackBytes);
		return userTrackInfo;
	}

	/* read and write dem bytes */

	/*
	 * build a byte array from a supplied object. NOTE: this may return null
	 */
	private byte[] getDemBytes(UserTrackInfo datObject) {
		byte[] demBytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutput oo = null;
		try {
			bos = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bos);
			oo.writeObject(datObject);
			demBytes = bos.toByteArray();
		} catch (IOException e) {
			Log.e(TAG, "Failed to build object byte array.");
		} finally {
			if (oo != null) {
				try {
					oo.close();
				} catch (IOException e) {
					Log.i(TAG, "Failed to close object output stream.");
				}
			}
		}
		return demBytes;
	}

	/*
	 * build a UserTrackInfo object from the supplied bytes
	 */
	private UserTrackInfo getDatObject(byte[] demBytes) {
		UserTrackInfo datObject = null;
		ByteArrayInputStream bis = null;
		ObjectInput oi = null;
		try {
			bis = new ByteArrayInputStream(demBytes);
			oi = new ObjectInputStream(bis);
			datObject = (UserTrackInfo) oi.readObject();
		} catch (IOException e) {
			Log.e(TAG, "Failed to build object from byte array (IOException).");
		} catch (ClassNotFoundException ce) {
			Log.e(TAG,
					"Failed to build object from byte array (ClassNotFoundException).");
		} finally {
			if (oi != null) {
				try {
					oi.close();
				} catch (IOException e) {
					Log.i(TAG, "Failed to close object input stream.");
				}
			}
		}
		return datObject;
	}
}
