package com.ipalandroid.questionview;

import com.ipalandroid.R;
import com.ipalandroid.common.Utilities;
import com.ipalandroid.common.Utilities.ConnectionResult;
import com.ipalandroid.common.Utilities.SubmissionResult;
import com.ipalandroid.login.LoginActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity displays the question to the user. It also allows the user to
 * send the result to the server.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class QuestionViewActivity extends Activity {

	//Strings used in UI
	private String CONNECTION_ERROR_MESSAGE;
	private String IPAL_INFO_INVALID;
	private String CONNECTING_IPAL_MESSAGE;
	private String REFRESHING_IPAL_MESSAGE;
	private String SUBMITTING_IPAL_MESSAGE;
	private String SUBMISSION_MESSAGE;
	private String FAILED_SUBMISSION_MESSAGE;
	private int ANSWER_SUBMITTED;
	private int ANSWER_NOT_SUBMITTED;
	
	//The QuestionFactory instance that is specific to this activity
	private QuestionFactory questionFactory; 

	//UI elements
	Button submitButton;
	ScrollView questionScrollView;
	TextView answerStatus;
	
	/**
	 * Refresh the question
	 */
	private void refresh() {
		QuestionViewCreator refresher = new QuestionViewCreator(REFRESHING_IPAL_MESSAGE);
		refresher.execute(questionFactory);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getStringResources();
		getIntResources();
		setContentView(R.layout.question_view);
		Utilities.setHeaderContent(findViewById(R.id.header),
				getString(R.string.question_view_header_text));
		
		// Getting variables from the intent.
		int passcode = getIntent().getIntExtra(LoginActivity.PASSCODE_EXTRA,
				Utilities.INT_FORMAT_ERROR);
		String username = getIntent().getStringExtra(
				LoginActivity.USERNAME_EXTRA);
		String url = getIntent().getStringExtra(LoginActivity.URL_EXTRA);
		//Initialize the QuestionFactory
		questionFactory = new QuestionFactory(url,
				username, passcode);
		QuestionViewCreator creator = new QuestionViewCreator(
				CONNECTING_IPAL_MESSAGE);
		creator.execute(questionFactory);

		/*
		 * Here we used the refresh button for testing. It is only for testing!
		 * We will change the implementation here to auto-refreshing or
		 * server-notified.
		 */
		answerStatus = (TextView) findViewById(R.id.answerSubmitted);
		((Button) findViewById(R.id.refreshButton))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						refresh();
						answerStatus.setText("");
					}
				});
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		//Refresh the question when a new intent is received.
		//Here we do not recreate the questionFactory instance,
		//because refreshing is supposed to use the original data.
		//If we need to customize the refreshing,
		//put the parameters in the intent sent to this activity
		//and get those parameters here.
		refresh();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//Unregister GCM here
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Register GCM here
	}

	
	/**
	 * This method gets the string resources used in this activity.
	 */
	private void getStringResources() {
		CONNECTION_ERROR_MESSAGE = getString(R.string.connection_problem_message);
		IPAL_INFO_INVALID = getString(R.string.ipal_info_invalid_message);
		CONNECTING_IPAL_MESSAGE = getString(R.string.connecting_ipal_message);
		REFRESHING_IPAL_MESSAGE = getString(R.string.refreshing_ipal_message);
		SUBMITTING_IPAL_MESSAGE = getString(R.string.submitting_ipal_message);
		SUBMISSION_MESSAGE = getString(R.string.submission_message);
		FAILED_SUBMISSION_MESSAGE = getString(R.string.failed_submission_message);
	}
	
	private void getIntResources(){
		ANSWER_SUBMITTED = SubmissionResult.ANSWER_SUBMITTED;
		ANSWER_NOT_SUBMITTED = SubmissionResult.ANSWER_NOT_SUBMITTED;
	}
	
	/**
	 * This method initializes all UI elements.
	 */
	private void initializeUIElements() {
		submitButton = (Button) findViewById(R.id.submitButton);
		questionScrollView = (ScrollView) findViewById(R.id.questionViewScrollView);
	}
	
	
	private class QuestionViewSubmission extends
			AsyncTask<QuestionFactory, Void, Integer>{
		ProgressDialog progressDialog;
		String dialogMessage;
		QuestionView questionView;
		
		
		public QuestionViewSubmission(QuestionView questionView, String dialogMessage){
			this.dialogMessage = dialogMessage;
			this.questionView = questionView;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(QuestionViewActivity.this);
			progressDialog.setMessage(dialogMessage);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancel(true);
					finish();
				}
			});
			progressDialog.show();
			
		}
		
		protected Integer doInBackground(QuestionFactory... qFactory) {
			if(questionView.sendResult())
				return ANSWER_SUBMITTED;
			return ANSWER_NOT_SUBMITTED;
			
		}
		
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			if(result == ANSWER_SUBMITTED)
			{
				Toast.makeText(QuestionViewActivity.this,
						SUBMISSION_MESSAGE, Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(QuestionViewActivity.this,
						FAILED_SUBMISSION_MESSAGE, Toast.LENGTH_SHORT).show();
			}
		}
		
		
		
	}
	
	
	
	
	/**
	 * AsyncTask class used for generating QuestionView Instance. It displays a
	 * progress dialog while checking at the background. If the loading was
	 * unsuccessful, we will exit from this activity.
	 */
	private class QuestionViewCreator extends
			AsyncTask<QuestionFactory, Void, Integer> {
		ProgressDialog progressDialog;
		QuestionFactory questionFactory;
		String dialogMessage;

		/**
		 * Constructor.
		 * 
		 * @param dialogMessage
		 *            the message to be displayed in the dialog.
		 */
		public QuestionViewCreator(String dialogMessage) {
			setDialogMessage(dialogMessage);
		}

		/**
		 * This method changes the dialog message.
		 * 
		 * @param dialogMessage
		 *            the message to be displayed in the dialog.
		 */
		public void setDialogMessage(String dialogMessage) {
			this.dialogMessage = dialogMessage;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//Create a progress dialog, used when loading the question
			progressDialog = new ProgressDialog(QuestionViewActivity.this);
			progressDialog.setMessage(dialogMessage);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancel(true);
					finish();
				}
			});
			progressDialog.show();
		}

		@Override
		protected Integer doInBackground(QuestionFactory... qFactory) {
			// TODO Auto-generated method stub
			questionFactory = qFactory[0];
			return questionFactory.loadQuestionView();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			//If error occurs while getting the question from the server,
			//display the error message and terminates the activity.
			if (result == ConnectionResult.CONNECTION_ERROR) {
				Toast.makeText(QuestionViewActivity.this,
						CONNECTION_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			if (result == ConnectionResult.RESULT_NOT_FOUND) {
				Toast.makeText(QuestionViewActivity.this, IPAL_INFO_INVALID,
						Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			// If the QuestionViewCreator loads the question successfully.
			final QuestionView questionView = questionFactory.getQuestionView();
			LayoutParams questionParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			initializeUIElements();
			// Remove previous question view if there is one.
			if (questionScrollView.getChildCount() > 0)
				questionScrollView.removeAllViews();
			//Add the new view
			questionScrollView.addView(
					questionView.getQuestionView(QuestionViewActivity.this),
					questionParams);
			submitButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!questionView.validateInput()) {
						Toast.makeText(QuestionViewActivity.this,
								getString(R.string.invalid_answer_message),
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					QuestionViewSubmission submit = new QuestionViewSubmission(questionView, SUBMITTING_IPAL_MESSAGE);
					submit.execute();
					//if (questionView.sendResult()) 
						//answerStatus.setText("Answer Submitted");
				}
			});
		}
	}
	
}
