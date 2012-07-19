package tjs.tuneramblr.meta.model;

/**
 * Models a tuneramblr user
 */
public class UserInfo {
	private String username;
	private String password;

	public UserInfo(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Retrieves the user's username
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Retrieves the user's password
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

}
