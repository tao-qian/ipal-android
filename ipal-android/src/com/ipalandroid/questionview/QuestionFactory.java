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
	 * @return the right QuestionView instance will display the HTML in Android
	 *         GUI.
	 */
	public static QuestionView getQuestionView(Document questionPage) {
		int type = 0;
		switch (type) {
		default:
			return null;
		}
	}
}
