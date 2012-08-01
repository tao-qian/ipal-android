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

	protected Document questionPage;//The HTML of the question
	protected String url;//URL of the HTML
	//User credentials used to send result and get question
	protected String username;
	protected int passcode;
	
	//HTML elements used to send result.
	protected String qText;
	protected String imageURL;
	protected int question_id;
	protected int active_question_id;
	protected int course_id;
	protected int user_id;
	protected int ipal_id;
	protected String instructor;
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
		getIPALInfoFromUI();
	}
	
	/**
	 * This method returns a view containing the question that is to be added to
	 * the QuestionViewActivity.
	 * @param c the context used to initialize the view
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
	
	/**
	 * This method get the IPAL info common to all QuestionViews from the HTML.
	 */
	private void getIPALInfoFromUI()
	{
		question_id = Integer.parseInt(questionPage.select("input[name=question_id]").attr("value"));
		active_question_id = Integer.parseInt(questionPage.select("input[name=active_question_id]").attr("value"));
		course_id = Integer.parseInt(questionPage.select("input[name=course_id]").attr("value"));
		user_id = Integer.parseInt(questionPage.select("input[name=user_id]").attr("value"));
		ipal_id = Integer.parseInt(questionPage.select("input[name=ipal_id]").attr("value"));
		instructor = questionPage.select("input[name=instructor]").attr("value");
	}
}
