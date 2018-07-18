package com.kin.ecosystem;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.support.annotation.IntDef;
import android.util.Log;
import java.lang.annotation.Retention;

class Logger {

	private static final String BASE_TAG = "KinEcosystem - ";

	private static boolean shouldLog;

	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;

	@IntDef({DEBUG, INFO, WARN, ERROR})
	@Retention(SOURCE)
	public @interface Priority {

	}

	private Logger() {
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
