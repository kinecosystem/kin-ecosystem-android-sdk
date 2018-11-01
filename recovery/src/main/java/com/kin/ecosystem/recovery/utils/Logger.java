package com.kin.ecosystem.recovery.utils;


import android.util.Log;
import com.kin.ecosystem.recovery.BuildConfig;

public class Logger {

	private static final String TAG = "kin.backup";
	private static boolean shouldLog = BuildConfig.DEBUG || Log.isLoggable(TAG, Log.DEBUG);

	private Logger() {
	}

	public static void d(String msg) {
		if (shouldLog) {
			Log.d(TAG, msg);
		}
	}

	public static void e(String msg, Throwable throwable) {
		if (shouldLog) {
			Log.e(TAG, msg, throwable);
		}
	}
}