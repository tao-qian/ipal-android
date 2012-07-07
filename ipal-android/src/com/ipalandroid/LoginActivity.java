package com.ipalandroid;

import org.jsoup.nodes.Document;

import com.ipalandroid.Utilities.SharedPreferenceKeys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This activity is responsible for displaying an
 * account setting form and reading user input.
 * @author Tao Qian, DePauw Open Source Development Team
 */
public class LoginActivity extends Activity {

	SharedPreferences prefs;
	EditText userNameEditText;
	EditText urlEditText;
	CheckBox saveInfoCheckBox;
	Button confirmButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		prefs = getPreferences(MODE_PRIVATE);
		userNameEditText = (EditText) findViewById(R.id.userNameEditText);
		urlEditText = (EditText) findViewById(R.id.urlEditText);
		saveInfoCheckBox = (CheckBox) findViewById(R.id.saveInfoCheckBox);
		confirmButton = (Button) findViewById(R.id.confirmButton);
		setUpElements();
	}

	/**
	 * This method sets up the UI elements dynamically. It should be called
	 * after calling setContentView() and initializing all the UI elements.
	 */
	private void setUpElements() {
		// Set the text of the header.
		Utilities.setHeaderContent(findViewById(R.id.header),
				getString(R.string.login_header_text));

		// If an account was already setup and info was saved, load the data
		// into UI.
		if (prefs.getBoolean(SharedPreferenceKeys.INFO_SAVED, false)) {
			saveInfoCheckBox.setChecked(true);
			userNameEditText.setText(prefs.getString(
					SharedPreferenceKeys.USERNAME, ""));
			urlEditText.setText(prefs.getString(SharedPreferenceKeys.URL, ""));
		}

		confirmButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String username = userNameEditText.getText().toString();
				String url = urlEditText.getText().toString();
				Boolean saveInfo = saveInfoCheckBox.isChecked();
				// Check whether the user input is valid
				if (!Utilities.validateUsername(username)) {
					Toast.makeText(LoginActivity.this,
							getString(R.string.invalid_username_message),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!Utilities.validateURL(url)) {
					Toast.makeText(LoginActivity.this,
							getString(R.string.invalid_url_message),
							Toast.LENGTH_SHORT).show();
					return;
				}
				Utilities.setPreference(prefs, saveInfo, username, url);
				
				// Start the QuestionViewActivity
				Intent intent = new Intent(LoginActivity.this,
						QuestionViewActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//Store user info on exit.
		String username = userNameEditText.getText().toString();
		String url = urlEditText.getText().toString();
		Boolean saveInfo = saveInfoCheckBox.isChecked();
		Utilities.storePreference(prefs, saveInfo, username, url);
		super.onDestroy();
	}
}