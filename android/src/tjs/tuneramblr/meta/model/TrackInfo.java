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
package tjs.tuneramblr.meta.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackInfo implements Parcelable {

	private String artist;
	private String album;
	private String track;

	// more to come

	/**
	 * Builds a music metadata object
	 * 
	 * @param artist
	 *            the artist value of the most recently played track track
	 * @param album
	 *            the album value of the most recently played track
	 * @param track
	 *            the name of the most recently played track
	 */
	public TrackInfo(String artist, String album, String track) {
		this.artist = artist;
		this.album = album;
		this.track = track;
	}

	/**
	 * The artist meta data
	 * 
	 * @return the artist name
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * The album meta data
	 * 
	 * @return the album name
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * The track meta data
	 * 
	 * @return the track name
	 */
	public String getTrack() {
		return track;
	}

	@Override
	public String toString() {
		return "TrackInfo [artist=" + artist + ", album=" + album + ", track="
				+ track + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(artist);
		dest.writeString(album);
		dest.writeString(track);
	}

	public static final Parcelable.Creator<TrackInfo> CREATOR = new Parcelable.Creator<TrackInfo>() {
		public TrackInfo createFromParcel(Parcel in) {
			String artist = in.readString();
			String album = in.readString();
			String track = in.readString();
			return new TrackInfo(artist, album, track);
		}

		public TrackInfo[] newArray(int size) {
			return new TrackInfo[size];
		}
	};

}
