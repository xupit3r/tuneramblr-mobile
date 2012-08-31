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
package tjs.tuneramblr.meta;

import java.io.IOException;

import org.json.JSONException;

import tjs.tuneramblr.data.TrackInfoDS;
import tjs.tuneramblr.meta.model.TrackInfo;
import tjs.tuneramblr.meta.model.Weather;
import tjs.tuneramblr.meta.weather.WeatherRequester;
import tjs.tuneramblr.meta.weather.WeatherUndergroundRequester;
import android.content.Context;
import android.location.Location;

/**
 * handles the retrieval of required meta data
 */
public class MetadataManager {

	/**
	 * Builds an object that can be used to request all kinds of meta data about
	 * the environment
	 */
	public MetadataManager() {
	}

	/**
	 * Get the current weather near the user
	 * 
	 * @param loc
	 *            the user's current location
	 * @return the weather near the user
	 * @throws IOException
	 * @throws JSONException
	 */
	public Weather getLocalWeather(Location loc) throws IOException,
			JSONException {
		Weather uWeather = null;

		// grab current weather for this location
		WeatherRequester requester = new WeatherUndergroundRequester(
				loc.getLatitude(), loc.getLongitude());
		uWeather = requester.retrieveCurrentWeather();

		return uWeather;
	}

	/**
	 * Retrieves information on the most recently played (or the track that was
	 * most recently started)
	 * 
	 * @return information on the most recently played track
	 */
	public TrackInfo getTrackInfo(Context context) {
		TrackInfoDS trackInfoDs = new TrackInfoDS(context);
		trackInfoDs.open();
		TrackInfo trackInfo = trackInfoDs.getLastRecordedTrack();
		trackInfoDs.close();

		/*
		 * if track info is null, simply return a track info object that
		 * contains empty strings (this will allow the UI populate without
		 * special conditions)
		 */
		return trackInfo == null ? new TrackInfo("", "", "") : trackInfo;
	}
}
