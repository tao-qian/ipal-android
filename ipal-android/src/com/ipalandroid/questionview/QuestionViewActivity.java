package com.ipalandroid.questionview;

import org.jsoup.nodes.Document;

import com.ipalandroid.R;
import com.ipalandroid.Utilities;
import com.ipalandroid.login.LoginActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This activity displays the question to the user. It also allows the user to
 * send the result to the server.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class QuestionViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_view);
		Utilities.setHeaderContent(findViewById(R.id.header),
				getString(R.string.question_view_header_text));
		//Getting variables from the intent.
		int passcode = getIntent().getIntExtra(LoginActivity.PASSCODE_EXTRA, -1);
		String username = getIntent().getStringExtra(LoginActivity.USERNAME_EXTRA);
		String url = getIntent().getStringExtra(LoginActivity.URL_EXTRA);
		//Here we post to the tempview.php to get the question page html.
		//Will add detailed code after getting the new tempview.php.
		Document questionPage = null;
		final QuestionView questionView = QuestionFactory
				.getQuestionView(questionPage,url,username,passcode);

		LinearLayout questionViewLayout = (LinearLayout) findViewById(R.id.questionViewLinearLayout);
		LayoutParams questionParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// addContentView(questionView.getQuestionView(), layoutParams);

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
				//questionView.sendResult();
			}
		});
	}
}
