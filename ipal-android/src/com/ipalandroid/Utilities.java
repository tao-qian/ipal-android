package com.ipalandroid;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.TextView;

/**
 * This class defines some static methods and constants.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class Utilities {

	/**
	 * This interface stores the keys used to access the
	 * preference of this app.
	 */
	public interface SharedPreferenceKeys {
		public final static String USERNAME = "username";
		public final static String URL = "url";
		public final static String IS_VALID = "isvalid";
	}

	/**
	 * This interface stores the attribute values
	 * used in Moodle/IPAL. 
	 * The last string segment of the constants in
	 * this class must be the name of the attribute,
	 * e.g. String LOGIN_FORM_ID = "login" is used to 
	 * 		find the HTML element which has an id of "login".
	 */
	interface MoodleHTMLContract
	{
		public final static String LOGIN_FORM_ID = "login";
		public final static String LOGIN_USERNAME_NAME = "username";
		public final static String LOGIN_PASSWORD_NAME = "password";
		public final static String SITE_INDEX_ID = "site-index";
		public final static String LOGIN_INDEX_ID = "login-index";
		public final static String NOT_LOGGED_IN_CLASS = "notloggedin";
	}
	
	/**
	 * This interface is used to store the attribute names
	 * used in HTML.
	 */
	interface HTMLAttribute
	{
		public final static String ID_ATTR = "id";
		public final static String ACTION_ATTR = "action";
		public final static String CLASS_ATTR = "class";
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
	 * This method validate whether the password is legal. Now it only checks
	 * whether the length of the password is bigger than 0.
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
	 * This method validate whether the passcode is legal. Now it only checks
	 * whether the passcode is a integer.
	 * 
	 * @param passcode
	 *            the passcode to be checked.
	 * @return the passcode as an integer if it is legal. -1 otherwise.
	 */
	public static int validatePasscode(String passcode) {
		int result = -1;
		try{
			result = Integer.parseInt(passcode);
		}
		catch (NumberFormatException e) {
			result = -1;
		}
		return result;
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
	 * This method tried to log into Moodle using the given username and
	 * password. It checks whether the username and the password are valid.
	 * 
	 * @param username
	 *            the username used.
	 * @param password
	 *            the password used.
	 * @param url
	 *            the url of the moodle
	 * @return true if the username and password are valid. false otherwise.
	 */
	public static boolean validateUser(String username, String password,
			String url) {
		if (!(validateUsername(username) && validatePassword(password) && validateURL(url)))
			return false;
        try {
			Document homePage = Jsoup.connect(url).get();
			Element loginForm = homePage.getElementById(MoodleHTMLContract.LOGIN_FORM_ID);
			String loginURL = loginForm.attr(HTMLAttribute.ACTION_ATTR);
			Document loginPage = Jsoup.connect(loginURL)
					.data(MoodleHTMLContract.LOGIN_USERNAME_NAME, username)
					.data(MoodleHTMLContract.LOGIN_PASSWORD_NAME,password).post();
			String loginInfo;
			/*
			 * Here we used the attributes of the body to identify whether
			 * the login is successful. 
			 * If the login is successful, we will be
			 * redirected to the home page, which has a body with an id of
			 * MoodleHTMLContract.SITE_INDEX_ID. Otherwise, we will be redirected to
			 * the login page, which has a body with an id of 
			 * MoodleHTMLContract.LOGIN_INDEX_ID.
			 * In the case of an unsuccessful login, the class attribute
			 * of body will contain the string MoodleHTMLContract.NOT_LOGGED_IN_CONTENT.
			 */
			try
			{
				loginInfo = loginPage.getElementById(MoodleHTMLContract.SITE_INDEX_ID).attr(HTMLAttribute.CLASS_ATTR);
			}
			catch (NullPointerException e) {
				//loginInfo = loginPage.getElementById(MoodleHTMLContract.LOGIN_INDEX_ID).attr(HTMLAttribute.CLASS_ATTR);
				return false;
			}
			if(loginInfo.contains(MoodleHTMLContract.NOT_LOGGED_IN_CLASS))
			{
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
}
