package com.ipalandroid.questionview;

import android.content.Context;
import android.view.View;
import org.jsoup.nodes.Document;

/**
 * This is a super class for question views. It's subclasses are responsible for
 * creating views from HTML and send answers to the server.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 * 
 */
public abstract class QuestionView {

	protected Document questionPage;
	protected String url;
	protected String username;
	protected int passcode;
	
	/**
	 * Super class constructor.
	 * @param questionPage the question page HTML used.
	 * @param url the Moodle URL.
	 * @param username the username of the user 
	 * @param passcode the passcode of the IPAL
	 */
	public QuestionView(Document questionPage, String url, String username, int passcode)
	{
		this.questionPage = questionPage;
		this.url = url;
		this.username = username;
		this.passcode = passcode;
	}
	
	/**
	 * This method returns a view containing the question that is to be added to
	 * the QuestionViewActivity.
	 * 
	 * @return a view to be added to the QuestionViewActivity.
	 */
	public abstract View getQuestionView(Context c);

	/**
	 * This method reads the user input and checks whether it is valid.
	 * 
	 * @return true if the input is valid, false if not.
	 */
	public abstract Boolean validateInput();

	/**
	 * This method sends the user result to the server. It can only be called
	 * after calling validateInput.
	 * 
	 * @return true if the result was sent successfully, false otherwise
	 */
	public abstract Boolean sendResult();
}
