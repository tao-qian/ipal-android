package com.ipalandroid.login;

import java.io.IOException;
import java.util.Map.Entry;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.ipalandroid.common.Utilities;
import com.ipalandroid.common.Utilities.ConnectionResult;

import android.util.Log;
import android.webkit.URLUtil;


/**
 * This class contains all the methods used to validate user info.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class UserValidater {
	private static String TAG="UserValidater";
	/**
	 * This interface stores the constants used to validate user.
	 */
	public interface UserValidationContract {
		/*
		 * The attribute values used. The last string segment of the constants
		 * in this class must be the name of the attribute, e.g. String
		 * LOGIN_FORM_ID = "login" is used to find the HTML element which has an
		 * id of "login".
		 */
		public final static String LOGIN_FORM_ID = "login";
		public final static String LOGIN_USERNAME_NAME = "username";
		public final static String LOGIN_PASSWORD_NAME = "password";
		public final static String SITE_INDEX_ID = "site-index";
		public final static String PAGE_SITE_INDEX_ID = "page-site-index";
		public final static String LOGIN_INDEX_ID = "login-index";
		public final static String NOT_LOGGED_IN_CLASS = "notloggedin";

		// The attribute names used in HTML.
		public final static String ID_ATTR = "id";
		public final static String ACTION_ATTR = "action";
		public final static String CLASS_ATTR = "class";
		public final static String LOGIN_IPAL_END_URL = "mod/ipal/ipallogin.php/";
		public final static String LOGIN_PAGE_END_URL = "login/index.php/";
	}

	private String username;
	private String password;
	private String url;
	private String endurl; // The string variable that will be added to the basic URL when logging in.

	/**
	 * Constructor.
	 * 
	 * @param username
	 *            the username used.
	 * @param password
	 *            the password used.
	 * @param url
	 *            the url of the moodle
	 */
	public UserValidater(String username, String password, String url) {
		this.username = username;
		this.password = password;
		this.url = url;
	}

	/**
	 * This method tried to log into Moodle using the given username and
	 * password. It checks whether the username and the password are valid.
	 * 
	 * @return result code to indicate whether the connection is successful.
	 */
	public int validateUser() {
		String validatedUsername = validateUsername(username);
		String validatedPassword = validatePassword(password);
		String protocolURL = validateURL(url, true);
		if (validatedUsername.equals(Utilities.STRING_FORMAT_ERROR)
				|| validatedPassword.equals(Utilities.STRING_FORMAT_ERROR)
				|| protocolURL.equals(Utilities.STRING_FORMAT_ERROR))
			return ConnectionResult.RESULT_NOT_FOUND;
		username = validatedUsername;
		password = validatedPassword;
		// Check for new version of IPAL with IPAL login.
		endurl = UserValidationContract.LOGIN_IPAL_END_URL;
		// First check with HTTPS protocol.
		int result = connectToMoodle(protocolURL);
		// If HTTPS protocol causes connection error, check with HTTP protocol.
		if (result == ConnectionResult.CONNECTION_ERROR) {
			protocolURL = validateURL(url, false);
			result = connectToMoodle(protocolURL);
		}
		if (result == ConnectionResult.CONNECTION_ERROR) {
			endurl = UserValidationContract.LOGIN_PAGE_END_URL;
			result = connectToMoodle(protocolURL);

			if (result == ConnectionResult.CONNECTION_ERROR) {
				protocolURL = validateURL(url, false);
				result = connectToMoodle(protocolURL);
			}
			Log.d(TAG, "line 107 and not connected to ipallogin");
		}
		url = protocolURL;
		return result;
	}

	/**
	 * This method connects to Moodle to check whether the user name and
	 * password are valid.
	 * 
	 * @param moodleURL
	 *            the URL of the Moodle home page, contains protocol.
	 * @return result code to indicate whether the connection is successful.
	 */
	private int connectToMoodle(String moodleURL) {
		try {
			//First get the login form, which contains the destination for the login POST
			//String loginURL = moodleURL + UserValidationContract.LOGIN_PAGE_END_URL;
			String loginURL = moodleURL + endurl;
			Document loginPage = Jsoup.connect(loginURL).followRedirects(true).get();
			Element loginForm = loginPage
					.getElementById(UserValidationContract.LOGIN_FORM_ID);
			Log.d(TAG, "115");
			if(loginForm == null)
				return ConnectionResult.INVALID_URL;

			//Get the destination for the login POST
			String loginFormURL = loginForm
					.attr(UserValidationContract.ACTION_ATTR);
			//Login with a POST and save the cookies
			Log.d(TAG, "123 and loginFormURL is "+loginFormURL);
			Response response = Jsoup.connect(loginFormURL)
					.data(UserValidationContract.LOGIN_USERNAME_NAME, username)
					.data(UserValidationContract.LOGIN_PASSWORD_NAME, password)
					.method(Method.POST).execute();
			//Now go to the Moodle home page with the cookies
			String responseString = response.body();
			Document ipalaccessform = response.parse();
			Log.d(TAG, "130 and response is "+responseString);
			Log.d(TAG, "132 and username and password are "+username+" and "+password);
			Element ipalbodytag = ipalaccessform.getElementById(UserValidationContract.PAGE_SITE_INDEX_ID);
			Log.d(TAG, "134 and the class is "+ipalbodytag.attr("class"));
			if(ipalbodytag.attr("class").contains("isloggedin")) {
				Log.d(TAG, "136 and isloggedin");
				return ConnectionResult.RESULT_FOUND;
			}
			String loginInfo;
			//loginInfo = document.getElementById(UserValidationContract.PAGE_SITE_INDEX_ID).attr(
					//UserValidationContract.CLASS_ATTR);
			Log.d(TAG, "134");
/**


			try {
				loginInfo = document.getElementById(
					UserValidationContract.PAGE_SITE_INDEX_ID).attr(
						UserValidationContract.CLASS_ATTR);
					Log.d(TAG, "136 and loginInfo is "+loginInfo);
				} catch (NullPointerException e) {
			}
*/
			Connection connection = Jsoup.connect(moodleURL);

			for (Entry<String, String> cookie : response.cookies().entrySet()) {
			    connection.cookie(cookie.getKey(), cookie.getValue());
			}
			//Required because in Moodle 2.4+,
			//depending on the global settings, the user may be redirected to
			//Site, My home or User Preferences.
			//And we only want to look at the Site page.
			Document loggedInPage = connection.data("redirect","0").get();

			/*
			 * Here we used the attributes of the body to identify whether the
			 * login is successful. If the login is successful, we will be
			 * redirected to the home page, which has a body with an id of
			 * UserValidationContract.SITE_INDEX_ID or an id of
			 * UserValidationContract.PAGE_SITE_INDEX_ID. Otherwise, we will be
			 * redirected to the login page, which has a body with an id of
			 * UserValidationContract.LOGIN_INDEX_ID. In the case of an
			 * unsuccessful login, the class attribute of body will contain the
			 * string UserValidationContract.NOT_LOGGED_IN_CLASS.
			 */
			try {
				loginInfo = loggedInPage.getElementById(
						UserValidationContract.SITE_INDEX_ID).attr(
						UserValidationContract.CLASS_ATTR);
				Log.d(TAG, "155 and loginInfo is "+loginInfo);
			} catch (NullPointerException e) {
				try {
					loginInfo = loggedInPage.getElementById(
							UserValidationContract.PAGE_SITE_INDEX_ID).attr(
							UserValidationContract.CLASS_ATTR);
					Log.d(TAG, "161");
				} catch (NullPointerException e2) {
					Log.d(TAG, "163");
					return ConnectionResult.RESULT_NOT_FOUND;
				}
			}
			if (loginInfo.contains(UserValidationContract.NOT_LOGGED_IN_CLASS)) {
				Log.d(TAG, "168 loginInfo is "+loginInfo);
				return ConnectionResult.RESULT_NOT_FOUND;
			} else {
				// The main page doesn't indicate that the person is logged in. So we check with the ipallogin.php page.

			}
		} catch (IOException e) {
			e.printStackTrace();
			return ConnectionResult.CONNECTION_ERROR;
		}
		catch(NullPointerException e)
		{
			return ConnectionResult.CONNECTION_ERROR;
		}
		return ConnectionResult.RESULT_FOUND;
	}

	/**
	 * This method formats the user name given by the user, it then validates
	 * whether the formatted user name is legal.
	 * 
	 * @param username
	 *            the user name to be checked
	 * @return the formatted user name if it is legal, STRING_FORMAT_ERROR
	 *         otherwise.
	 */
	private static String validateUsername(String username) {
		// Remove all white space.
		username = username.replace(" ", "");
		if (username.length() > 0)
			return username;
		return Utilities.STRING_FORMAT_ERROR;
	}

	/**
	 * This method formats the URL given by the user, it then validates whether
	 * the formatted URL is legal. It adds "https://" to the beginning of the
	 * URL if it does not contain one. It adds "/" to the end of the URL also.
	 * 
	 * 
	 * @param url
	 *            the URL to be checked
	 * @param useHTTPS
	 *            if true, https protocol will be used, otherwise use http.
	 * @return the formatted URL if it is legal, STRING_FORMAT_ERROR otherwise.
	 */
	private static String validateURL(String url, Boolean useHTTPS) {
		String wrongProtocol = "http://";
		String rightProtocol = "https://";
		if (!useHTTPS) {
			wrongProtocol = "https://";
			rightProtocol = "http://";
		}

		url = url.replace(" ", "");
		if (!url.endsWith("/"))
			url = url + "/";
		url = url.replace(wrongProtocol, rightProtocol);
		if (!url.startsWith(rightProtocol))
			url = rightProtocol + url;
		if (URLUtil.isValidUrl(url))
			return url;
		return Utilities.STRING_FORMAT_ERROR;
	}

	/**
	 * This method formats the password given by the user, it then validates
	 * whether the formatted password is legal.
	 * 
	 * 
	 * @param password
	 *            the password to be checked
	 * @return the formatted password if it is legal, STRING_FORMAT_ERROR
	 *         otherwise.
	 */
	private static String validatePassword(String password) {
		password = password.trim();
		if (password.length() > 0)
			return password;
		return Utilities.STRING_FORMAT_ERROR;
	}

	/**
	 * This method validate whether the passcode is legal. Now it only checks
	 * whether the passcode is a integer.
	 * 
	 * @param passcode
	 *            the passcode to be checked.
	 * @return the passcode as an integer if it is legal. INT_FORMAT_ERROR
	 *         otherwise.
	 */
	public static int validatePasscode(String passcode) {
		int result = Utilities.INT_FORMAT_ERROR;
		try {
			result = Integer.parseInt(passcode);
		} catch (NumberFormatException e) {
			result = Utilities.INT_FORMAT_ERROR;
		}
		return result;
	}

	/**
	 * Getter for URL, can only be called when validation is successful.
	 * 
	 * @return the validated URL
	 */
	public String getURL() {
		return url;
	}

	/**
	 * Getter for username, can only be called when validation is successful.
	 * 
	 * @return the validated username
	 */
	public String getUsername() {
		return username;
	}
}
