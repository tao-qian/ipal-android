package com.ipalandroid.questionview;

import com.ipalandroid.R;
import com.ipalandroid.Utilities;
import com.ipalandroid.Utilities.ConnectionResult;
import com.ipalandroid.login.LoginActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * This activity displays the question to the user. It also allows the user to
 * send the result to the server.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class QuestionViewActivity extends Activity {

	private String CONNECTION_ERROR_MESSAGE;
	private String IPAL_INFO_INVALID;
	private String CONNECTING_IPAL_MESSAGE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getStringResources();
		
		//Getting variables from the intent.
		int passcode = getIntent().getIntExtra(LoginActivity.PASSCODE_EXTRA, -1);
		String username = getIntent().getStringExtra(LoginActivity.USERNAME_EXTRA);
		String url = getIntent().getStringExtra(LoginActivity.URL_EXTRA);
		QuestionFactory questionFactory = new QuestionFactory(url, username, passcode);
		QuestionViewCreator creator = new QuestionViewCreator();
		creator.execute(questionFactory);
	}
	
	/**
	 * This method gets the string resources used in this activity.
	 */
	private void getStringResources() {
		CONNECTION_ERROR_MESSAGE = getString(R.string.connection_problem_message);
		IPAL_INFO_INVALID = getString(R.string.ipal_info_invalid_message);
		CONNECTING_IPAL_MESSAGE = getString(R.string.connecting_ipal_message);
	}
	
	/**
	 * AsyncTask class used for generating QuestionView Instance. It displays
	 * a progress dialog while checking at the background. If the loading 
	 * was unsuccessful, we will exit from this activity.
	 */
	private class QuestionViewCreator extends AsyncTask<QuestionFactory, Void, Integer >
	{
		ProgressDialog progressDialog;
		QuestionFactory questionFactory;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(QuestionViewActivity.this);
			progressDialog.setMessage(CONNECTING_IPAL_MESSAGE);
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
			if(result == ConnectionResult.CONNECTION_ERROR)
			{
				Toast.makeText(QuestionViewActivity.this,CONNECTION_ERROR_MESSAGE , Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			if(result == ConnectionResult.RESULT_NOT_FOUND)
			{
				Toast.makeText(QuestionViewActivity.this, IPAL_INFO_INVALID, Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			progressDialog.dismiss();
			//If the QuestionViewCreator loads the question successfully.
			Utilities.setHeaderContent(findViewById(R.id.header),
					getString(R.string.question_view_header_text));
			final QuestionView questionView = questionFactory.getQuestionView();
			ScrollView questionScrollView = (ScrollView) findViewById(R.id.questionViewScrollView);
			LayoutParams questionParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			questionScrollView.addView(questionView.getQuestionView(QuestionViewActivity.this), questionParams);
			setContentView(R.layout.question_view);
			Button submitButton = (Button) findViewById(R.id.submitButton);
			submitButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!questionView.validateInput()) {
						Toast.makeText(QuestionViewActivity.this,
								getString(R.string.invalid_answer_message),
								Toast.LENGTH_SHORT).show();
						return;
					}
					questionView.sendResult();
				}
			});
		}
	}
}
