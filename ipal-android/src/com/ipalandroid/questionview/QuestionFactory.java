package com.ipalandroid.questionview;

import org.jsoup.nodes.Document;

/**
 * This class is responsible for creating different question views.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class QuestionFactory {


	/**
	 * This method creates the right QuestionView instance from an Document
	 * instance. 
	 * 
	 * @param questionPage
	 *            the HTML of the question page returned from the server
	 * @param url the url of the Moodle
	 * @param username  the username of the user
	 * @param passcode the passcode of the IPAL
	 * @return the right QuestionView instance will display the HTML in Android
	 *         GUI.
	 */
	public static QuestionView getQuestionView(Document questionPage, String url, String username, int passcode) {
		int type = 0;
		switch (type) {
		default:
			return null;
		}
	}
}
