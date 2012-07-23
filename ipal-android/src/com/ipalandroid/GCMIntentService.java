package com.ipalandroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	public static final String SENDER_ID = "42332721478";
	
	public GCMIntentService() {
        super(SENDER_ID);
        //Log.d("GCMIntentService", senderId);
    }
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.w("RegID", arg1+"   ");
		//TODO: send the regID to the server
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.w("RegID", arg1+"   ");
		
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// TODO Auto-generated method stub
		return super.onRecoverableError(context, errorId);
	}
	
	
	
}
