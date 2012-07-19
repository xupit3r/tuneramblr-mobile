package tjs.tuneramblr.meta.weather;

import java.io.IOException;

import org.json.JSONException;

import tjs.tuneramblr.meta.model.Weather;

public interface WeatherRequester {

	/**
	 * Retrieves the current weather conditions
	 * 
	 * @return weather information
	 */
	Weather retrieveCurrentWeather() throws IOException, JSONException;

}
