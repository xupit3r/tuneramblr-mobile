package tjs.tuneramblr.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import tjs.tuneramblr.meta.model.UserInfo;
import android.content.Context;

/**
 * Provides access and storage of user information.
 * 
 */
public class UserInfoDS {
	private static final String USERS_FILE_NAME = "userinfo.dat";

	private static final int USERNAME_INDEX = 0;
	private static final int PASSWORD_INDEX = 1;

	private Context context;

	public UserInfoDS(Context context) {
		this.context = context;
	}

	public UserInfo readUserInfo() throws IOException {
		UserInfo retVal = null;

		// read the username/password from the filesystem
		FileInputStream is = context.openFileInput(USERS_FILE_NAME);
		Scanner reader = new Scanner(is);
		String[] inArr = new String[2];
		int idx = 0;
		while (reader.hasNextLine()) {
			inArr[idx++] = reader.nextLine();
		}
		reader.close();

		// setup the user info
		retVal = new UserInfo(inArr[USERNAME_INDEX], inArr[PASSWORD_INDEX]);

		return retVal;
	}

	public void saveUserInfo(String username, String password)
			throws FileNotFoundException {
		FileOutputStream os = context.openFileOutput(USERS_FILE_NAME,
				Context.MODE_PRIVATE);
		PrintWriter writer = new PrintWriter(os);
		writer.println(username);
		writer.println(password);
		writer.close();
	}
}
