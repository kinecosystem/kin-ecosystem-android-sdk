package com.kin.ecosystem.core;

import android.util.Log;
import com.kin.ecosystem.core.Log.Priority;

public class Logger {

	private static final String BASE_TAG = "KinEcosystem - ";

	private static boolean shouldLog;

	private Logger() {
	}

	public static void log(com.kin.ecosystem.core.Log ecosystemLog) {
		ecosystemLog.log();
	}

	public static void log(@Priority final int priority, final String tag, final String content) {
		if (shouldLog) {
			Log.println(priority, getTag(tag), content);
		}
	}

	public static void enableLogs(final boolean enableLogs) {
		Logger.shouldLog = enableLogs;
	}

	public static boolean isEnabled() {
		return shouldLog;
	}

	private static String getTag(String tag) {
		return BASE_TAG + tag;
	}
}
 