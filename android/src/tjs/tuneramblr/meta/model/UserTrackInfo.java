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

import java.io.Serializable;

import android.location.Location;

/**
 * Representation of data to be passively collected based on user's interaction
 * with the multimedia player.
 */
public class UserTrackInfo implements Serializable {

	private static final long serialVersionUID = -6140782012533256290L;

	private Location location;
	private Weather weather;
	private long currentTime;
	private TrackInfo trackInfo;

	/**
	 * Builds a track info capture object (UserTrackInfo)
	 * 
	 * @param location
	 *            the location of this track info capture with this user track
	 *            info
	 * @param weather
	 *            the weather at the time of the track info capture with this
	 *            user track info
	 * @param currentTime
	 *            the time of the track info capture
	 * @param trackInfo
	 *            information related to the current track
	 */
	public UserTrackInfo(Location location, Weather weather, long currentTime,
			TrackInfo trackInfo) {
		super();
		this.location = location;
		this.weather = weather;
		this.currentTime = currentTime;
		this.trackInfo = trackInfo;
	}

	/**
	 * Get the location of this capture
	 * 
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Get the weather attached to this capture
	 * 
	 * @return the weather
	 */
	public Weather getWeather() {
		return weather;
	}

	/**
	 * Get the time of this capture
	 * 
	 * @return the currentTime
	 */
	public long getCurrentTime() {
		return currentTime;
	}

	/**
	 * Get the track information attached to this capture
	 * 
	 * @return the trackInfo
	 */
	public TrackInfo getTrackInfo() {
		return trackInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (currentTime ^ (currentTime >>> 32));
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((trackInfo == null) ? 0 : trackInfo.hashCode());
		result = prime * result + ((weather == null) ? 0 : weather.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UserTrackInfo)) {
			return false;
		}
		UserTrackInfo other = (UserTrackInfo) obj;
		if (currentTime != other.currentTime) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (trackInfo == null) {
			if (other.trackInfo != null) {
				return false;
			}
		} else if (!trackInfo.equals(other.trackInfo)) {
			return false;
		}
		if (weather == null) {
			if (other.weather != null) {
				return false;
			}
		} else if (!weather.equals(other.weather)) {
			return false;
		}
		return true;
	}

}
