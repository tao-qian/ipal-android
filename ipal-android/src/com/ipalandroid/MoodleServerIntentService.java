package com.ipalandroid;

import java.io.IOException;

import org.jsoup.Jsoup;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * This class is responsible sending and removing regId from Moodle Server. regId is used
 * by GCM to send push notifications to the application
 * 
 * @author Ngoc Nguye, DePauw Open Source Development Team
 */
public class MoodleServerIntentService extends IntentService{
	
	public static final String PASSCODE = "passcode";
	public static final String USERNAME = "username";
	public static final String URL = "url";
	public static final String REGID = "regId";
	public static final String JOB = "job";
	public static final String JOB_SEND = "send";
	public static final String JOB_REMOVE = "remove";
	
	public MoodleServerIntentService() {
		super("MoodleServerIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String dataString = intent.getStringExtra(JOB);
		
		//Case 1: send the RegId from Moodle Server
		if (dataString.equals(JOB_SEND)) {
			String username = intent.getStringExtra(USERNAME);
			String currentPasscode = intent.getStringExtra(PASSCODE);
			String url = intent.getStringExtra(URL);
			String regId = intent.getStringExtra(REGID);
			try {
				Jsoup.connect(url+"/mod/ipal/tempview.php")
				.data("user", username)
				.data("p", currentPasscode)
				.data("r", regId)
				.get();
				Log.w("MoodleServerIntentService", "Send RegID to server for user: " + username);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Case 2: Remove the RegId from Moodle Server
		else if (dataString.equals(JOB_REMOVE)) {
			String username = intent.getStringExtra(USERNAME);
			String currentPasscode = intent.getStringExtra(PASSCODE);
			String url = intent.getStringExtra(URL);
			try {
				Jsoup.connect(url+"/mod/ipal/tempview.php")
				.data("user", username)
				.data("p", currentPasscode)
				.data("unreg", 1+"")
				.get();
				Log.w("MoodleServerIntentService", "Remove RegId for user: " + username);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
