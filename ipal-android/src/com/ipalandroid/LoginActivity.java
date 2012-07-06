package com.ipalandroid;

import com.ipalandroid.Utilities.SharedPreferenceKeys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	/** Called when the activity is first created. */
	
	final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); 
        setUpElements();
    }
    
    /**
     * This method sets up the UI elements dynamically.
     * It should be called after calling setContentView().
     */
    private void setUpElements()
    {
    	//Set the text of the header.
    	Utilities.setHeaderContent(findViewById(R.id.header), getString(R.string.login_header_text));
    	
    	final EditText userNameEditText = (EditText) findViewById(R.id.userNameEditText);
    	final EditText urlEditText = (EditText) findViewById(R.id.urlEditText);
    	final CheckBox saveInfoCheckBox = (CheckBox)findViewById(R.id.saveInfoCheckBox);
    	final Button confirmButton = (Button)findViewById(R.id.confirmButton);
    	final Button discardButton = (Button)findViewById(R.id.discardButton);
    
    	//If an account was already setup and info was saved, load the data into UI.
    	if(prefs.getBoolean(SharedPreferenceKeys.INFO_SAVED, false))
    	{
    		saveInfoCheckBox.setChecked(true);
    		userNameEditText.setText(prefs.getString(SharedPreferenceKeys.USERNAME, ""));
    		urlEditText.setText(prefs.getString(SharedPreferenceKeys.URL, ""));
    	}
    	
    	confirmButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String username = userNameEditText.getText().toString();
				String url = urlEditText.getText().toString();
				Boolean saveInfo = saveInfoCheckBox.isChecked();
				//Check whether the user input is valid
				if(!Utilities.validateUsername(username))
				{
					Toast.makeText(LoginActivity.this, getString(R.string.invalid_username_message), Toast.LENGTH_SHORT).show();
					return;
				}
				if(!Utilities.validateURL(url))
				{
					Toast.makeText(LoginActivity.this, getString(R.string.invalid_url_message), Toast.LENGTH_SHORT).show();
					return;
				}
				Utilities.setPreference(prefs,saveInfo , username, url);
				finish();
			}
		});
    	
    	discardButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    }
}