package com.ipalandroid.login;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.webkit.URLUtil;

import com.ipalandroid.Utilities;
import com.ipalandroid.Utilities.ConnectionResult;

/**
 * This class contains all the methods used to validate user info.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class UserValidationUtilities {

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
		public final static String LOGIN_INDEX_ID = "login-index";
		public final static String NOT_LOGGED_IN_CLASS = "notloggedin";

		// The attribute names used in HTML.
		public final static String ID_ATTR = "id";
		public final static String ACTION_ATTR = "action";
		public final static String CLASS_ATTR = "class";

		public final static String LOGIN_PAGE_END_URL = "login/index.php";
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
	 * @return result code to indicate whether the connection is successful.
	 */
	public static int validateUser(String username, String password, String url) {
		username = validateUsername(username);
		password = validatePassword(password);
		String protocolURL = validateURL(url, true);
		if (username.equals(Utilities.STRING_FORMAT_ERROR)
				|| password.equals(Utilities.STRING_FORMAT_ERROR)
				|| url.equals(Utilities.STRING_FORMAT_ERROR))
			return ConnectionResult.RESULT_NOT_FOUND;
		protocolURL = protocolURL + UserValidationContract.LOGIN_PAGE_END_URL;
		//First check with HTTPS protocol.
		int result = connectToMoodle(username, password, protocolURL);
		//If HTTPS protocol causes connection error, check with HTTP protocol.
		if(connectToMoodle(username, password, protocolURL) == ConnectionResult.CONNECTION_ERROR)
		{
			protocolURL = validateURL(url, false);
			protocolURL = protocolURL + UserValidationContract.LOGIN_PAGE_END_URL;
			result = connectToMoodle(username, password, protocolURL);
		}
		return result;
	}

	/**
	 * This method connects to Moodle to check whether the user name and
	 * password are valid.
	 * 
	 * @param username
	 *            the username used.
	 * @param password
	 *            the password used.
	 * @param url
	 *            the url of the moodle
	 * @return result code to indicate whether the connection is successful.
	 */
	private static int connectToMoodle(String username, String password,
			String url) {
		try {
			Document loginPage = Jsoup.connect(url).get();
			Element loginForm = loginPage
					.getElementById(UserValidationContract.LOGIN_FORM_ID);
			String loginURL = loginForm
					.attr(UserValidationContract.ACTION_ATTR);
			Document loggedInPage = Jsoup.connect(loginURL)
					.data(UserValidationContract.LOGIN_USERNAME_NAME, username)
					.data(UserValidationContract.LOGIN_PASSWORD_NAME, password)
					.post();
			String loginInfo;
			/*
			 * Here we used the attributes of the body to identify whether the
			 * login is successful. If the login is successful, we will be
			 * redirected to the home page, which has a body with an id of
			 * UserValidationContract.SITE_INDEX_ID. Otherwise, we will be
			 * redirected to the login page, which has a body with an id of
			 * UserValidationContract.LOGIN_INDEX_ID. In the case of an
			 * unsuccessful login, the class attribute of body will contain the
			 * string UserValidationContract.NOT_LOGGED_IN_CONTENT.
			 */
			try {
				loginInfo = loggedInPage.getElementById(
						UserValidationContract.SITE_INDEX_ID).attr(
						UserValidationContract.CLASS_ATTR);
			} catch (NullPointerException e) {
				// loginInfo =
				// loginPage.getElementById(MoodleHTMLContract.LOGIN_INDEX_ID).attr(HTMLAttribute.CLASS_ATTR);
				return ConnectionResult.RESULT_NOT_FOUND;
			}
			if (loginInfo.contains(UserValidationContract.NOT_LOGGED_IN_CLASS)) {
				return ConnectionResult.RESULT_NOT_FOUND;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
}
