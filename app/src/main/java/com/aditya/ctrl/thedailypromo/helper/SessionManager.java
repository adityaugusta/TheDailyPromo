package com.aditya.ctrl.thedailypromo.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

	private static String TAG = SessionManager.class.getSimpleName();

	private static final String PREF_NAME = "TheDailyPromo";
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

	SharedPreferences pref;
	Editor editor;
	Context mContext;
	int PRIVATE_MODE = 0;

	public SessionManager(Context context) {
		mContext = context;
		pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setLogin(boolean isLoggedIn) {
		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
		editor.commit();
	}
	
	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}
}
