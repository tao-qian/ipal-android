package com.ipalandroid;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ipalandroid.questionview.QuestionViewActivity;

/**
 * Created by W.F.Junkin on 6/12/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Needed for handleNotification
    public static String STR_PUSH = "pushNotification";

    private static final String TAG = "FCMService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        handleNotification(remoteMessage.getNotification().getBody());
    }
    private void handleNotification(String body) {
        Log.d(TAG, "In handleNotification method");
        Intent dialogIntent = new Intent(this, QuestionViewActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(dialogIntent);
    }
}