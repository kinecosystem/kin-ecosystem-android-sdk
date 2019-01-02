package com.ecosystem.kin.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoBackupRepo {

	private final static String AUTO_BACKUP_PREFERENCE_FILE_KEY = "auto_backup_file";
	private final static String NO_BACKUP_PREFERENCE_FILE_KEY = "no_backup_file";
	private final static String NUMBER_OF_SESSIONS_KEY = "NUMBER_OF_SESSIONS_KEY";


	private static SharedPreferences getAutoBackupSharedPreferences(Context context) {
		return context
			.getSharedPreferences(AUTO_BACKUP_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
	}

	private static SharedPreferences getNoBackupSharedPreferences(Context context) {
		return context
			.getSharedPreferences(NO_BACKUP_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
	}

	@NonNull
	public static int getNumberOfBackupedSessions(Context context) {
		return getNumberOfSessions(getAutoBackupSharedPreferences(context));
	}

	@NonNull
	public static int getNumberOfSessions(Context context) {
		return getNumberOfSessions(getNoBackupSharedPreferences(context));
	}

	private static int getNumberOfSessions(SharedPreferences sharedPreferences) {
		return sharedPreferences.getInt(NUMBER_OF_SESSIONS_KEY, 0);
	}


	public static void incrementNumberOfSessions(Context context) {
		incrementNumberOfSessions(getNoBackupSharedPreferences(context));
		incrementNumberOfSessions(getAutoBackupSharedPreferences(context));
	}

	private static void incrementNumberOfSessions(SharedPreferences sharedPreferences) {
		int numberOfSessions = sharedPreferences.getInt(NUMBER_OF_SESSIONS_KEY, 0) + 1;
		sharedPreferences.edit().putInt(NUMBER_OF_SESSIONS_KEY, numberOfSessions).apply();
	}
}
