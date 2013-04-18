package com.ipalandroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.ipalandroid.common.Utilities;
import com.ipalandroid.login.LoginActivity;
import com.ipalandroid.questionview.QuestionViewActivity;
/**
 * This class provides a service to handle GCM callbacks. All GCM-related classes have to be 
 * in the main package.
 * 
 * @author Ngoc Nguyen, DePauw Open Source Development Team
 *
 */
public class GCMIntentService extends GCMBaseIntentService {
	
	
	public GCMIntentService() {
        super(Utilities.SENDER_ID);
        //Log.d("GCMIntentService", senderId);
    }
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context c, Intent arg1) {
		//PendingIntent refreshIntent = new PendingIntent(c,QuestionViewActivity.class);
		//TODO: Build a notification when the app is minimized
		Intent notificationIntent = new Intent(c, QuestionViewActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent =
                PendingIntent.getActivity(c, 0, notificationIntent, 0);
		
		NotificationCompat.Builder mBuilder = 
			new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("You Got New Question!" + arg1)
			.setContentText("Touch to view question")
			.setAutoCancel(true)
			.setContentIntent(intent);
		
	
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Log.w("GCM Testing","Start buidling GCM notification");
		notificationManager.notify(0, mBuilder.build());
		
		//Send an intent to QuestionView activity.
		//So that it will refresh
		Intent refreshIntent = new Intent(c,QuestionViewActivity.class);
		//This flag is required when starting activity outside an activity.
		refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(refreshIntent);
		
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		//Send the regID to the server, handled by the Login Activity class
		LoginActivity.sendToServer(regId);
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.w("RegID Unregister", arg1+"   ");
		
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// TODO Auto-generated method stub
		return super.onRecoverableError(context, errorId);
	}
	
	
	
}
