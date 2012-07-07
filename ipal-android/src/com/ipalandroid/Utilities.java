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
		public final static String INFO_SAVED = "infosaved";
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
	public static Boolean validateUsername(String username) {
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
	public static Boolean validateURL(String url) {
		if (url.length() > 0)
			return true;
		return false;
	}

	/**
	 * This method updates the shared preference. Each parameter will be stored
	 * in its respective field in the shared preference.
	 * 
	 * @param prefs
	 *            the shared preference instance used.
	 * @param to
	 *            be stored with the key INFO_SAVED.
	 * @param username
	 *            to be stored with the key USERNAME.
	 * @param url
	 *            to be stored with the key URL.
	 */
	public static synchronized void setPreference(SharedPreferences prefs,
			Boolean saveInfo, String username, String url) {
		Editor editor = prefs.edit();
		editor.putBoolean(SharedPreferenceKeys.INFO_SAVED, saveInfo);
		editor.putString(SharedPreferenceKeys.USERNAME, username);
		editor.putString(SharedPreferenceKeys.URL, url);
		editor.commit();
	}

	/**
	 * This method stores the preference when the application is exiting. It
	 * saves the user information is auto-saving is turned on. Otherwise, no
	 * user information will be stored in the preference.
	 * 
	 * @param prefs
	 *            the shared preference instance used.
	 * @param saveInfo
	 *            whether to save user information. To be stored with the key
	 *            INFO_SAVED.
	 * @param username
	 *            to be stored with the key USERNAME if auto-saving is turned
	 *            on.
	 * @param url
	 *            to be stored with the key URL if auto-saving is turned on.
	 */
	public static synchronized void storePreference(SharedPreferences prefs,
			Boolean saveInfo, String username, String url) {
		if (saveInfo)
			setPreference(prefs, saveInfo, username, url);
		else
			setPreference(prefs, saveInfo, "", "");
	}
}
