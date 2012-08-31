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

/**
 * Enumeration of the types of checkins
 */
public enum CheckinType {
	USER_LIKE("user_like"), SKIP("skip"), FULLY_LISTENED("fully_listened");

	private String typeName;

	CheckinType(String typeName) {
		this.typeName = typeName;
	}

	public String toValue() {
		return this.typeName;
	}

	public static CheckinType fromValue(String s) {
		CheckinType retVal = null;
		if (s == null) {
			retVal = null;
		} else if (USER_LIKE.toString().equals(s)) {
			retVal = USER_LIKE;
		} else if (SKIP.toString().equals(s)) {
			retVal = SKIP;
		} else {
			retVal = FULLY_LISTENED;
		}

		return retVal;
	}
}
