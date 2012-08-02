package com.ipalandroid.questionview.GCM;

import org.jsoup.Jsoup;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.ipalandroid.common.Utilities;

public class GCMRegistrationManager {
	/**
	 * Send the regId to the server
	 * @param regId
	 */
	public static void sendToServer(String regId) {
		// TODO: Get url, username, and passcode from shared preference
		String url = "";
		String username = "";
		String passcode = ""; 
		Jsoup.connect(url+"/mod/ipal/tempview.php?user="+username+"&p="+passcode+"&r="+regId);
	}
	
	/**
	 * Register device with GCM service
	 * 
	 * @param Context c
	 */
	public static void registerGCM(Context c) {
		//Setup GCM
		GCMRegistrar.checkDevice(c);
		GCMRegistrar.checkManifest(c);
		
		String regId = GCMRegistrar.getRegistrationId(c);
		Log.w("Login Activity regID", regId+"a");
		//Log.w("regID", GCMRegistrar.getRegistrationId(this));
		if (regId.equals("")) {
		  GCMRegistrar.register(c, Utilities.SENDER_ID);
		  Log.w("Login Activity regID", GCMRegistrar.getRegistrationId(c));
		} else {
		  Log.v("Login Activity ABCD", "Already registered");
		}
	}
	
	/**
	 * Unregister device from GCM
	 * 
	 * @param Context c
	 */
	public static void unregisterGCM(Context c) {
		String regId = GCMRegistrar.getRegistrationId(c);
		if (!regId.equals("")) {
			GCMRegistrar.unregister(c);
		}
	}
}
