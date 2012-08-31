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
package tjs.tuneramblr.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * This is a utility class to handle HTTP work.
 */
public class HttpUtil {

	private static final String CHAR_ENCODING = "UTF-8";
	private static final String POST_CONTENT_TYPE = "application/x-www-form-urlencoded;charset="
			+ CHAR_ENCODING;

	private static HttpUtil HTTP_UTIL = null;

	private HttpUtil() {
		// do nothing!
	}

	/**
	 * Returns the sole instance of this class.
	 * 
	 * @return the instance of this class
	 */
	public static HttpUtil getInstance() {
		if (HTTP_UTIL == null) {
			HTTP_UTIL = new HttpUtil();
		}
		return HTTP_UTIL;
	}

	/**
	 * POSTs to a URL, but does not write data to the URL...only attaches
	 * request properties
	 * 
	 * @param uri
	 *            the URI that represents the URL to POST to
	 * @param queryString
	 *            (optional) the query string to include (write to the request
	 *            body) in the POST request
	 * @return a string representing the request response
	 * @throws IOException
	 */
	public String makePost(URI uri, String queryString) throws IOException {
		// first create a URL
		URL url = uri.toURL();
		return makePost(url, queryString);
	}

	private String makePost(URL url, String queryString) throws IOException {
		String responseString = null;

		// the output buffer to write to the resource
		byte[] outBuff = queryString.getBytes(CHAR_ENCODING);

		// setup the connection for POST
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Accept-Charset", CHAR_ENCODING);
		httpConn.setRequestProperty("Content-Type", POST_CONTENT_TYPE);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setFixedLengthStreamingMode(outBuff.length);

		OutputStream requestOs = null;
		try {

			// write the query string to the request body
			requestOs = new BufferedOutputStream(httpConn.getOutputStream());
			requestOs.write(outBuff);

		} finally {
			if (requestOs != null) {
				try {
					requestOs.close();
				} catch (IOException ioe) {
					// no big deal
				}
			}

		}

		InputStream responseIs = null;
		try {
			// pull out the response
			responseIs = new BufferedInputStream(httpConn.getInputStream());
			responseString = StringUtil.getInstance().buildStringFromStream(
					responseIs);

		} finally {
			if (responseIs != null) {
				try {
					responseIs.close();
				} catch (IOException ioe) {
					// no big deal
				}
			}
			httpConn.disconnect();
		}

		return responseString;
	}

	/**
	 * Takes in a valid URL containing both the URL and query portions.
	 * 
	 * NOTE: this should only be used when the requested resource (response) is
	 * not expected to be large (as this streams the entire resource into memory
	 * as a String)
	 * 
	 * TODO: define "large"
	 * 
	 * @param uri
	 *            the URI to use when making the GET request
	 * @return a string representing the requested resource
	 * @throws IOException
	 */
	public String makeGet(URI uri) throws IOException {
		String responseString = null;

		// first create a URL
		URL url = uri.toURL();

		// then open a connection
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		try {
			InputStream responseIs = new BufferedInputStream(
					httpConn.getInputStream());
			responseString = StringUtil.getInstance().buildStringFromStream(
					responseIs);

		} finally {
			httpConn.disconnect();
		}

		return responseString;
	}

	/**
	 * makes and HTTP GET request to a URL.
	 * 
	 * NOTE: this should only be used when the requested resource (response) is
	 * not expected to be large (as this streams the entire resource into memory
	 * as a String)
	 * 
	 * TODO: define "large"
	 * 
	 * @param urlStr
	 *            the url to use when making the GET request
	 * @param params
	 *            the set of parameters to include in the GET request
	 * @return
	 */
	public String makeGet(String urlStr) throws IOException {
		String responseString = null;

		// first create a URL
		URL url = new URL(urlStr);

		// then open a connection
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		try {
			InputStream responseIs = new BufferedInputStream(
					httpConn.getInputStream());
			responseString = StringUtil.getInstance().buildStringFromStream(
					responseIs);

		} finally {
			httpConn.disconnect();
		}

		return responseString;
	}
}
