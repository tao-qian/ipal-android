package com.ipalandroid;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.TextView;

/**
 * This class defines some static methods and constants.
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class Utilities {

	interface SharedPreferenceKeys {
		public final static String USERNAME = "username";
		public final static String URL = "url";
		public final static String IS_VALID = "isvalid";
	}

	/**
	 * This method sets the text of a header.
	 * 
	 * @param header
	 *            the header used
	 * @param text
	 */
	public static void setHeaderContent(View header, String text) {
		TextView headerText = (TextView) header
				.findViewById(R.id.headerTextView);
		headerText.setText(text);
	}

	/**
	 * This method validate whether the user name is legal. Now it only checks
	 * whether the length of the user name is bigger than 0.
	 * 
	 * @param username
	 *            the user name to be checked
	 * @return true if legal, false if not
	 */
	private static Boolean validateUsername(String username) {
		if (username.length() > 0)
			return true;
		return false;
	}

	/**
	 * This method validate whether the URL is legal. Now it only checks whether
	 * the length of the URL is bigger than 0.
	 * 
	 * @param url
	 *            the URL to be checked.
	 * @return true if legal, false if not.
	 */
	private static Boolean validateURL(String url) {
		if (url.length() > 0)
			return true;
		return false;
	}
	
	/**
	 * This method validate whether the password is legal. Now it only checks whether
	 * the length of the password is bigger than 0.
	 * 
	 * @param password
	 *            the password to be checked.
	 * @return true if legal, false if not.
	 */
	private static Boolean validatePassword(String password) {
		if (password.length() > 0)
			return true;
		return false;
	}

	/**
	 * This method updates the shared preference. Each parameter will be stored
	 * in its respective field in the shared preference.
	 * 
	 * @param prefs
	 *            the shared preference instance used.
	 * @param isValid
	 *            be stored with the key IS_VALID.
	 * @param username
	 *            to be stored with the key USERNAME.
	 * @param url
	 *            to be stored with the key URL.
	 */
	public static synchronized void setPreference(SharedPreferences prefs,
			Boolean isValid, String username, String url) {
		Editor editor = prefs.edit();
		editor.putBoolean(SharedPreferenceKeys.IS_VALID, isValid);
		editor.putString(SharedPreferenceKeys.USERNAME, username);
		editor.putString(SharedPreferenceKeys.URL, url);
		editor.commit();
	}
	
	/**
	 * This method tried to log into Moodle using the given username and password.
	 * It checks whether the username and the password are valid.
	 * @param username the username used.
	 * @param password the password used.
	 * @param url the url of the moodle
	 * @return true if the username and password are valid. false otherwise.
	 */
	public static boolean validateUser(String username, String password, String url)
	{
		if(!(validateUsername(username)&&validatePassword(password)&&validateURL(url)))
			return false;
		if(password.length()<3)
			return false;
		return true;
	}
}
