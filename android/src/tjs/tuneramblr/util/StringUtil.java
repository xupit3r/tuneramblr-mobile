package tjs.tuneramblr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtil {

	private static final StringUtil THE_INSTANCE = new StringUtil();

	private static final int BUFFER_SIZE = 1024;

	private StringUtil() {

	}

	public static StringUtil getInstance() {
		return THE_INSTANCE;
	}

	// IO Helpers //

	public String buildStringFromStream(InputStream is) throws IOException {
		StringBuilder builder = new StringBuilder();
		char[] buff = new char[BUFFER_SIZE];
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		int charsRead = 0;
		do {
			charsRead = reader.read(buff, 0, BUFFER_SIZE);
			builder.append(buff);
		} while (charsRead > -1);

		return builder.toString();
	}

}
