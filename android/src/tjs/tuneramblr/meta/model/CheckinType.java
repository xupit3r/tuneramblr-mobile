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
