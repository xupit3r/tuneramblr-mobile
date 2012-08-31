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
