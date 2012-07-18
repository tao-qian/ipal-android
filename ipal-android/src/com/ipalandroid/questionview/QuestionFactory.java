package com.ipalandroid.questionview;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ipalandroid.Utilities.ConnectionResult;
import com.ipalandroid.Utilities.TempViewPostContract;

/**
 * This class is responsible for creating different question views.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class QuestionFactory {

	private Document questionPage;
	private String url;
	private String username;
	private int passcode;
	private QuestionView questionView;
	
	/**
	 * Constructor.
	 * @param url the url of the Moodle page
	 * @param username the username used
	 * @param passcode the passcode of the IPAL
	 */
	public QuestionFactory(String url, String username, int passcode)
	{
		this.url = url;
		this.username = username;
		this.passcode = passcode;
	}
	
	/**
	 * This method posts to the tempview.php. It then checks whether
	 * a valid result is returned. If true, the QuestionView instance
	 * will be initialized.
	 * @return connection result indicating whether a result is found.
	 */
	public int loadQuestionView()
	{
		try {
			questionPage = Jsoup.connect(url+TempViewPostContract.URL_SEGMENT)
					.data(TempViewPostContract.USER, username)
					.data(TempViewPostContract.PASSCODE, String.valueOf(passcode))
					.post();
		} catch (IOException e) {
			return  ConnectionResult.CONNECTION_ERROR;
		}
		//Add detail here after the question identifier of tempview.php is changed.
		int type = 0;
		switch (type) {
		default:
			questionView = null;
			return ConnectionResult.CONNECTION_ERROR;
		}
		
	}
	
	/**
	 * This method creates the right QuestionView instance from an Document
	 * instance. Must be called after loadQuestionView().
	 * @return the right QuestionView instance will display the HTML in Android
	 *         GUI.
	 */
	public QuestionView getQuestionView() {
		return questionView;
	}
}
