package com.kin.ecosystem;

import android.util.Log;
import com.kin.ecosystem.Log.Priority;

public class Logger {

	private static final String BASE_TAG = "KinEcosystem - ";

	private static boolean shouldLog;

	private Logger() {
	}

	public static void log(com.kin.ecosystem.Log ecosystemLog) {
		ecosystemLog.log();
	}

	public static void log(@Priority final int priority, final String tag, final String content) {
		if (shouldLog) {
			Log.println(priority, getTag(tag), content);
		}
	}

	static void enableLogs(final boolean enableLogs) {
		Logger.shouldLog = enableLogs;
	}

	private static String getTag(String tag) {
		return BASE_TAG + tag;
	}
}
