package com.ipalandroid.login;

import com.ipalandroid.R;
import com.ipalandroid.common.Utilities;
import com.ipalandroid.common.Utilities.ConnectionResult;
import com.ipalandroid.common.Utilities.SharedPreferenceKeys;
import com.ipalandroid.questionview.QuestionViewActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/*
 * Copyright 2013 DePauw Open Source Development Team
 * Modified by W. F. Junkin III 2017 to use Firebase Cloud Messaging instead of GCM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/**
 * This activity is responsible for displaying an account setting form and
 * reading user input.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 * @author Ngoc Nguyen, DePauw Open Source Development Team
 * @author W. F. Junkin, Eckerd College
 */
public class LoginActivity extends Activity {

	static private String TAG = "LoginActivity";
	//Keys used to identify extra data passed with intent.
	public static final String PASSCODE_EXTRA = "passcode_extra";
	public static final String URL_EXTRA = "url_extral";
	public static final String USERNAME_EXTRA = "username_extra";
	public static final String REFRESHEDTOKEN_EXTRA = "refreshedtoken_extra";
	//Preference used to store user data.
	private SharedPreferences prefs;
	//UI elements
	private EditText userNameEditText;
	private EditText urlEditText;
	private EditText passwordEditText;
	private CheckBox saveInfoCheckBox;
	private Button confirmButton;
	//User input. 
	private static String url;
	private static String username;
	private String password;
	private UserValidater userValidater;
	//String used in UI
	private String INVALID_URL_MESSAGE;
	private String INVALID_USER_MESSAGE;
	private String INVALID_PASSCODE_FORMAT_MESSAGE;
	private String CONNECTION_ERROR_MESSAGE;
	private String CHECKING_USER_MESSAGE;
	private LinearLayout loginLayout;
	private InputMethodManager inputManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initializeUIElements();
		getStringResources();
		setUpElements();
		getHistorySettings();
		userValidater = null;
	}

	/**
	 * This method sets up the UI elements dynamically. It should be called
	 * after calling setContentView(), getHistorySettings(),
	 * initializeUIElements() and getStringResources().
	 */
	private void setUpElements() {
		// Set the text of the header.
		Utilities.setHeaderContent(findViewById(R.id.header),
				getString(R.string.login_header_text));

		//Creates an input manager. Added by Kevin Courtade
		inputManager = (InputMethodManager) LoginActivity.this.
				getSystemService(Context.INPUT_METHOD_SERVICE); 
		
		loginLayout.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				//Requests focus from other elements
				loginLayout.requestFocus();
				//Minimizes the virtual keyboard.
				inputManager.hideSoftInputFromWindow(
				        LoginActivity.this.getCurrentFocus().getWindowToken(),
				        InputMethodManager.HIDE_NOT_ALWAYS); 
			}
			
		});
		
		

		confirmButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//Validate the login information
				// Get settings from the UI.
				username = userNameEditText.getText().toString();
				password = passwordEditText.getText().toString();
				url = urlEditText.getText().toString();
				//Check whether the user data is valid in a separate thread.
				UserChecker userChecker = new UserChecker();
				userChecker.execute(username,password,url);
			}
		});
	}

	/**
	 * This methods initiates the preference instance and retrieve history
	 * settings from the preference.
	 */
	private void getHistorySettings() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		username = prefs.getString(SharedPreferenceKeys.USERNAME, "");
		url = prefs.getString(SharedPreferenceKeys.URL, "");
		userNameEditText.setText(username);
		urlEditText.setText(url);
	}

	@Override
	protected void onPause() {
		// Store user info on exit.
		if (saveInfoCheckBox.isChecked())
			Utilities.setPreference(prefs, username, url);
		super.onPause();
	}

	/**
	 * This method initializes all UI elements.
	 */
	private void initializeUIElements() {
		userNameEditText = (EditText) findViewById(R.id.userNameEditText);
		urlEditText = (EditText) findViewById(R.id.urlEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		saveInfoCheckBox = (CheckBox) findViewById(R.id.saveInfoCheckBox);
		confirmButton = (Button) findViewById(R.id.confirmButton);
		loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
	}

	/**
	 * This method gets the string resources used in this activity.
	 */
	private void getStringResources() {
		INVALID_URL_MESSAGE = getString(R.string.invalid_url_message);
		INVALID_USER_MESSAGE = getString(R.string.invalid_account_settings_message);
		INVALID_PASSCODE_FORMAT_MESSAGE = getString(R.string.invalid_passcode_format_message);
		CONNECTION_ERROR_MESSAGE = getString(R.string.connection_problem_message);
		CHECKING_USER_MESSAGE = getString(R.string.checking_user_message);
	}
	
	/**
	 * AsyncTask class used for validating the user. It displays
	 * a progress dialog while checking at the background.
	 */
	private class UserChecker extends AsyncTask<String, Void, Integer >
	{
		Boolean dialogCanceled;
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setMessage(CHECKING_USER_MESSAGE);
			dialogCanceled = false;
			progressDialog.setOnCancelListener(new OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {

					dialogCanceled = true;
				}
			});
			progressDialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			userValidater = new UserValidater(params[0], params[1],params[2]);
			return userValidater.validateUser();
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(dialogCanceled)
				return;
			boolean isValid = false;
			if(result == ConnectionResult.RESULT_FOUND)
				isValid = true;
			progressDialog.dismiss();
			// If valid, show a dialog ask for the passcode
			if (isValid)
			{
				username = userValidater.getUsername();
				url = userValidater.getURL();
				userNameEditText.setText(username);
				urlEditText.setText(url);
				final Context c = LoginActivity.this;
				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				// Set an EditText view to get user input 
				EditText passCodeEditText = new EditText(c);
				passCodeEditText.setHint(R.string.login_passcode_text);
				final EditText input = new EditText(c);
				AlertDialog alert = builder.setView(input).setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				}).setCancelable(false)
				.setTitle(R.string.passcode_dialog_title)
				.create();
				
				alert.setOnShowListener(new DialogInterface.OnShowListener() {
					
					public void onShow(final DialogInterface dialog) {
						Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				        b.setOnClickListener(new View.OnClickListener() {

				            public void onClick(View view) {
				                //Get the passcode and validate it.
								int passcode = UserValidater.validatePasscode(input.getText().toString());
								if(passcode<0)//If passcode is invalid, notify the user without dismissing the dialog
								{
									Toast.makeText(getApplicationContext(), INVALID_PASSCODE_FORMAT_MESSAGE, Toast.LENGTH_SHORT).show();
									return;
								}
								//If passcode is valid the token will be refreshed in QuestionViewActivity
								// Start the QuestionViewActivity
								Intent intent = new Intent(LoginActivity.this,
										QuestionViewActivity.class);
								intent.putExtra(PASSCODE_EXTRA, passcode);
								intent.putExtra(USERNAME_EXTRA, username);
								intent.putExtra(URL_EXTRA, url);
								startActivity(intent);
								
				                //Dismiss once everything is OK.
				                dialog.dismiss();
				            }
				        });
					}
				});
				alert.show();
			}//Notify the user otherwise
			else if( result == ConnectionResult.RESULT_NOT_FOUND)
				Toast.makeText(LoginActivity.this, INVALID_USER_MESSAGE,
						Toast.LENGTH_SHORT).show();
			else if(result == ConnectionResult.INVALID_URL)
				Toast.makeText(LoginActivity.this, "Invalid URL" + INVALID_URL_MESSAGE, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(LoginActivity.this, "Connection Error" + CONNECTION_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
		}
		
	}
	
}
