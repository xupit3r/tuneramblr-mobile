package tjs.tuneramblr.meta.weather;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import tjs.tuneramblr.TuneramblrConstants;
import tjs.tuneramblr.meta.model.Weather;
import tjs.tuneramblr.util.HttpUtil;

/**
 * Makes requests to the weather underground weather service
 */
public class WeatherUndergroundRequester implements WeatherRequester {

	/* the API key to use when making requests to the service */
	private static final String WEATHER_UNDERGROUND_API_KEY = TuneramblrConstants.WEATHER_UNDERGROUND_API;

	private static final String BASE_SERVICE_URL = "http://api.wunderground.com/api/";

	private static final String GEO_LOOKUP = "geolookup";

	private static final String CONDITIONS_FEATURE = "conditions";

	private static final String JSON_FORMAT = "json";

	/* JSON response keys */
	private static final String CURRENT_CONDITIONS = "current_observation";
	private static final String CURRENT_QUALITATIVE = "weather";

	// we will use the F for now
	private static final String CURRENT_TEMPERATURE = "temp_f";

	// temperature values
	private static final String TEMP_COLD = "Cold";
	private static final String TEMP_WARM = "Warm";
	private static final String TEMP_HOT = "Hot";

	private double lat;
	private double lng;

	public WeatherUndergroundRequester(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	private String buildUrl() {
		String retVal = null;

		String queryString = "/q/" + lat + "," + lng;
		String formatString = "." + JSON_FORMAT;
		String getQuery = queryString + formatString;
		retVal = BASE_SERVICE_URL + WEATHER_UNDERGROUND_API_KEY + "/"
				+ GEO_LOOKUP + "/" + CONDITIONS_FEATURE + getQuery;
		return retVal;
	}

	/*
	 * OK, so this guy is expected to take in the current temperature and return
	 * some qualitative description of that temperature (e.g. 30 F -> COLD)
	 */
	private String getQualitativeTemperature(int weatherVal) {
		String retVal = null;
		if (weatherVal <= 40) {
			retVal = TEMP_COLD;
		} else if (weatherVal <= 70) {
			retVal = TEMP_WARM;
		} else {
			retVal = TEMP_HOT;
		}
		return retVal;
	}

	@Override
	public Weather retrieveCurrentWeather() throws IOException, JSONException {
		Weather retVal = null;
		String response = HttpUtil.getInstance().makeGet(buildUrl());

		// parse the JSON response into a usable mapping of keys/values
		JSONObject respJson = new JSONObject(response);

		// pull out the current conditions object and extract a qualitative
		// description of the current conditions and the current temperature
		JSONObject curConds = respJson.getJSONObject(CURRENT_CONDITIONS);
		String qualitative = curConds.getString(CURRENT_QUALITATIVE);
		String temperature = getQualitativeTemperature(curConds
				.getInt(CURRENT_TEMPERATURE));

		// build the comma separated string of weather stuff
		String weatherStr = qualitative + "," + temperature;

		// create a weather object using the built string
		retVal = new Weather(weatherStr);
		return retVal;
	}

}
