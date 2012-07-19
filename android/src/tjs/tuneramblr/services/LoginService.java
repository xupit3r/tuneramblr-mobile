package tjs.tuneramblr.services;

import java.io.IOException;
import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import tjs.tuneramblr.TuneramblrConstants;
import tjs.tuneramblr.data.UserInfoDS;
import tjs.tuneramblr.util.HttpUtil;
import tjs.tuneramblr.util.TuneramblrUrlHelper;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class LoginService extends IntentService {

	private static final String TAG = "LoginService";

	private static final String LOGIN_AUTH_RESULT = "authresult";

	public LoginService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// execute the login procedure
		boolean authenticated = login(intent);
		// pass this along to the right place to update the UI
	}

	// attempts a login
	private boolean login(Intent intent) {
		boolean retVal = false;

		// pull the username and password
		String username = intent
				.getStringExtra(TuneramblrConstants.EXTRA_USERNAME_KEY);
		String password = intent
				.getStringExtra(TuneramblrConstants.EXTRA_PASSWORD_KEY);

		URI loginUri = TuneramblrUrlHelper.getInstance().getLoginHttpPostUrl();
		String loginQueryUrl = TuneramblrUrlHelper.getInstance()
				.buildLoginQueryString(username, password);
		String responseString = null;
		try {

			// try to login
			responseString = HttpUtil.getInstance().makePost(loginUri,
					loginQueryUrl);

			// ok, are we authenticated?
			// parse the response and pull the response message
			// TODO: handle auth key return (when implemented)
			JSONObject loginResultJson = new JSONObject(responseString);
			boolean authResult = loginResultJson.getBoolean(LOGIN_AUTH_RESULT);

			if (authResult) {
				// if authorized, save the username and password
				UserInfoDS userInfoDs = new UserInfoDS(getApplicationContext());
				userInfoDs.saveUserInfo(username, password);
				retVal = true;
			} else {
				// set the return value (the authenticated value) to false
				retVal = false;
			}

		} catch (IOException e) {
			Log.e(TAG, "Whoops!  That login totally failed! " + e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, "Whoops!  That login totally failed! " + e.getMessage());
		}

		return retVal;

	}
}