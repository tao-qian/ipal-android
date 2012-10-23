package com.ipalandroid.questionview;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ipalandroid.common.Utilities.ConnectionResult;


/**
 * This class is responsible for creating different question views.
 * Here we do not return the question view instance directly, instead,
 * we save the question view first and return it when requested.
 * This is because saving the question view itself might fail and the
 * user needs to know whether it is successful.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class QuestionFactory {

	/**
	 * This interface stores the constants used to post to tempview.php.
	 */
	public interface TempViewPostContract {
		public final static String URL_SEGMENT = "mod/ipal/tempview.php";
		public final static String USER = "user";
		public final static String PASSCODE = "p";
	}

	/**
	 * This interface stores the constants used to identify which type of question
	 * we are working with. Must be the same as the identifiers returned by the server.
	 */
	public interface QuestionType {
		public final static String TRUE_FALSE_QUESTION = "truefalse";
		public final static String MULTIPLE_CHOICE_QUESTION = "multichoice";
		public final static String ESSAY_QUESTION = "essay";
		public final static String ERROR_INVALID_USERNAME = "invalidusername";
		public final static String ERROR_INVALID_PASSCODE = "invalidpasscode";
		public final static String ERROR_NO_CURRENT_QUESTION = "nocurrentquestion";
	}
	
	private Document questionPage;//HTML of the question
	private String url;//URL of the question page
	private String username;
	private int passcode;
	private QuestionView questionView;//The product to be returned.
	
	
	
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
			e.printStackTrace();
			return  ConnectionResult.CONNECTION_ERROR;
		}
		
		String type = questionPage.select("p[id=questiontype]").text();
		//Not using a switch statement here because the identifiers are not integers.
		if(type.equals(QuestionType.ESSAY_QUESTION))
		{
			questionView = new EssayQuestionView(questionPage, url, username, passcode);
			return ConnectionResult.RESULT_FOUND;
		}
		else if(type.equals(QuestionType.MULTIPLE_CHOICE_QUESTION))
		{
			questionView = new MutipleChoiceQuesionView(questionPage, url, username, passcode);
			return ConnectionResult.RESULT_FOUND;
		}
		else if (type.equals(QuestionType.TRUE_FALSE_QUESTION))
		{
			questionView = new MutipleChoiceQuesionView(questionPage, url, username, passcode);
			return ConnectionResult.RESULT_FOUND;
		}
		else if (type.equals(QuestionType.ERROR_INVALID_PASSCODE))
		{
			questionView = null;
			return ConnectionResult.RESULT_NOT_FOUND;
		}
		else if (type.equals(QuestionType.ERROR_INVALID_USERNAME))
		{
			questionView = null;
			return ConnectionResult.RESULT_NOT_FOUND;
		}
		else if (type.equals(QuestionType.ERROR_NO_CURRENT_QUESTION))
		{
			questionView = null;
			return ConnectionResult.RESULT_NOT_FOUND;
		}
		return ConnectionResult.RESULT_NOT_FOUND;
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
