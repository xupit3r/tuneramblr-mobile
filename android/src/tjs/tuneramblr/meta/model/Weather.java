package tjs.tuneramblr.meta.model;

/**
 * models possible weather data to be collected.
 */
public class Weather {
	private Double temperature;
	private Double light;
	private Double pressure;
	private Double humidity;

	private String weatherMeta;

	/**
	 * Builds weather meta data using information gathered from the Android
	 * sensors
	 * 
	 * @param temperature
	 *            temperature pulled from the sensor
	 * @param light
	 *            light from the sensor
	 * @param pressure
	 *            pressure from the sensor
	 * @param humidity
	 *            humidity from the sensor
	 */
	public Weather(Double temperature, Double light, Double pressure,
			Double humidity) {
		super();
		this.temperature = temperature;
		this.light = light;
		this.pressure = pressure;
		this.humidity = humidity;
	}

	/**
	 * Builds weather data from a predefined meta string. This should be used
	 * whenever making requests to an outside source/service
	 * 
	 * @param weatherMeta
	 *            the meta data string that represents this weather meta object
	 */
	public Weather(String weatherMeta) {
		this.weatherMeta = weatherMeta;
	}

	public Double getTemperature() {
		return temperature;
	}

	public Double getLight() {
		return light;
	}

	public Double getPressure() {
		return pressure;
	}

	public Double getHumidity() {
		return humidity;
	}

	private String buildWeatherMeta() {
		String retVal = temperature + "," + light + "," + pressure + ","
				+ humidity;
		return retVal;
	}

	public String toString() {
		// this should generate human friendly outputs for the provided weather
		// elements. e.g. "humid, hot, sunny" (i.e. take the numbers that the
		// phone provides and translate them into some meaningful meta-data)
		if (weatherMeta == null) {
			weatherMeta = buildWeatherMeta();
		}
		return weatherMeta;
	}
}
