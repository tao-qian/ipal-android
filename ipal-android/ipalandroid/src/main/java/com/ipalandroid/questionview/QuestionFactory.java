package com.ipalandroid.questionview;

import android.util.Log;

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
	private static String TAG = "QuestionFactory";
	/**
	 * This interface stores the constants used to post to tempview.php.
	 */
	public interface TempViewPostContract {
		public final static String URL_SEGMENT = "mod/ipal/tempview.php";
		public final static String USER = "user";
		public final static String PASSCODE = "p";
		public final static String REFRESHED_TOKEN = "r";
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
	private String refreshedToken;
	private QuestionView questionView;//The product to be returned.
	
	
	
	/**
	 * Constructor.
	 * @param url the url of the Moodle page
	 * @param username the username used
	 * @param passcode the passcode of the ipal
	 * @param refreshedToken the token of the individual app.
	 */
	public QuestionFactory(String url, String username, int passcode, String refreshedToken)
	{
		this.url = url;
		this.username = username;
		this.passcode = passcode;
		this.refreshedToken = refreshedToken;
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
			Log.d(TAG, "debug79 in QuestionFactory and regid is "+refreshedToken);
			Log.d(TAG, "debug81 in QuestionFactory and url is "+url+TempViewPostContract.URL_SEGMENT);
			questionPage = Jsoup.connect(url+TempViewPostContract.URL_SEGMENT)
					.data(TempViewPostContract.USER, username)
					.data(TempViewPostContract.PASSCODE, String.valueOf(passcode))
                    .data(TempViewPostContract.REFRESHED_TOKEN, refreshedToken)
					.post();
		} catch (IOException e) {
			e.printStackTrace();
			return  ConnectionResult.CONNECTION_ERROR;
		}
		
		String type = questionPage.select("p[id=questiontype]").text();
		//Not using a switch statement here because the identifiers are not integers.
		try{
			Log.d(TAG, "95 and type is " + type);
			if(type.equals(QuestionType.ESSAY_QUESTION))
			{
				Log.d(TAG, "90 in QuestionFactory and is essay question");
				Log.d(TAG, "91 and legend is " + questionPage.select("legend").text());
				Log.d(TAG, "92 and url is " + url);
				Log.d(TAG, "93 and username is " + username);
				Log.d(TAG, "94 and passcode is " + passcode);
				questionView = new EssayQuestionView(questionPage, url, username, passcode);
				Log.d(TAG, "96 after questionView");

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
			else if (type.equals(QuestionType.ERROR_NO_CURRENT_QUESTION))
			{
				Log.d(TAG, "120 in nocurrentquestion");
				questionView = new NoQuestion(questionPage, url, username, passcode);
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
			return ConnectionResult.RESULT_NOT_FOUND;
		}
		//TODO What exception was being thrown again?
		catch(Exception e)
		{
			return ConnectionResult.RESULT_NOT_FOUND;
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
