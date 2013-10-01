package com.tripitintegration.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {

	private static Context mContext;
	private static final String PREF_NAME = "ALTOUR_SHARED_PREFERENCES";
	private static SharedPreferences mPreferences;
	
	// Tripit preference keys
	public static String TRIPIT_OAUTH_REQUEST_TOKEN = "TRIPIT_REQUEST_TOKEN";
	public static String TRIPIT_OAUTH_REQUEST_TOKEN_SECRET = "TRIPIT_REQUEST_TOKEN_SECRET";
	public static String TRIPIT_OAUTH_ACCESS_TOKEN = "TRIPIT_ACCESS_TOKEN";
	public static String TRIPIT_OAUTH_ACCESS_TOKEN_SECRET = "TRIPIT_ACCESS_TOKEN_SECRET";

	// constructor
	public SharedPref(Context c) {
		mContext = c;
	}

	// for boolean value
	public void writeBoolean(String key, boolean value) {
		getEditor().putBoolean(key, value).commit();
	}

	public boolean readBoolean(String key, boolean defValue) {
		return getPreferences().getBoolean(key, defValue);
	}

	// for integer value
	public void writeInteger(String key, int value) {
		getEditor().putInt(key, value).commit();

	}

	public int readInteger(String key, int defValue) {
		return getPreferences().getInt(key, defValue);
	}

	// for String value
	public void writeString(String key, String value) {
		getEditor().putString(key, value).commit();

	}

	public String readString(String key, String defValue) {
		return getPreferences().getString(key, defValue);
	}

	// for float value
	public void writeFloat(String key, float value) {
		getEditor().putFloat(key, value).commit();
	}

	public float readFloat(String key, float defValue) {
		return getPreferences().getFloat(key, defValue);
	}

	// for long value
	public void writeLong(String key, long value) {
		getEditor().putLong(key, value).commit();
	}

	public long readLong(String key, long defValue) {
		return getPreferences().getLong(key, defValue);
	}

	@SuppressWarnings("static-access")
	private SharedPreferences getPreferences() {
		if (mPreferences == null) {
			mPreferences = mContext.getSharedPreferences(PREF_NAME, mContext.MODE_PRIVATE);	
		}
		return mPreferences;
	}

	public Editor getEditor() {
		return getPreferences().edit();
	}

}
