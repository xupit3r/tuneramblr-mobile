package tjs.tuneramblr.meta.location;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Carries out the grunt work to retrieve the last location.
 * 
 * pulled from:
 * http://android-developers.blogspot.com/2011/06/deep-dive-into-location.html
 */
public class LastLocationFinder implements LocationFinder {

	protected static String TAG = "LastLocationFinder";

	protected LocationListener locationListener;
	protected LocationManager locationManager;
	protected Criteria criteria;
	protected Context context;

	/**
	 * Construct a new Legacy Last Location Finder.
	 * 
	 * @param context
	 *            Context
	 */
	public LastLocationFinder(Context context) {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		/*
		 * Coarse accuracy is specified here to get the fastest possible result.
		 * The calling Activity will likely (or have already) request ongoing
		 * updates using the Fine location provider.
		 */
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		this.context = context;
	}

	public Location getLastBestLocation(int minDistance, long minTime) {
		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MAX_VALUE;

		/*
		 * Iterate through all the providers on the system, keeping note of the
		 * most accurate result within the acceptable time limit. If no result
		 * is found within maxTime, return the newest Location.
		 */
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider : matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if ((time < minTime && accuracy < bestAccuracy)) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time > minTime && bestAccuracy == Float.MAX_VALUE
						&& time < bestTime) {
					bestResult = location;
					bestTime = time;
				}
			}
		}

		/*
		 * If the best result is beyond the allowed time limit, or the accuracy
		 * of the best result is wider than the acceptable maximum distance,
		 * request a single update. This check simply implements the same
		 * conditions we set when requesting regular location updates every
		 * [minTime] and [minDistance]. Prior to Gingerbread "one-shot" updates
		 * weren't available, so we need to implement this manually.
		 */
		if (locationListener != null
				&& (bestTime > minTime || bestAccuracy > minDistance)) {
			String provider = locationManager.getBestProvider(criteria, true);
			if (provider != null)
				locationManager.requestLocationUpdates(provider, 0, 0,
						singeUpdateListener, context.getMainLooper());
		}

		return bestResult;
	}

	/**
	 * This one-off {@link LocationListener} simply listens for a single
	 * location update before unregistering itself. The one-off location update
	 * is returned via the {@link LocationListener} specified in
	 * {@link setChangedLocationListener}.
	 */
	protected LocationListener singeUpdateListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d(TAG,
					"Single Location Update Received: "
							+ location.getLatitude() + ","
							+ location.getLongitude());
			if (locationListener != null && location != null)
				locationListener.onLocationChanged(location);
			locationManager.removeUpdates(singeUpdateListener);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	/**
	 * {@inheritDoc}
	 */
	public void setChangedLocationListener(LocationListener l) {
		locationListener = l;
	}

	/**
	 * {@inheritDoc}
	 */
	public void cancel() {
		locationManager.removeUpdates(singeUpdateListener);
	}

}