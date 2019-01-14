package com.kin.ecosystem.core.data.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class SettingsDataSourceLocal implements SettingsDataSource.Local {

	private static final String SETTINGS_PREF_NAME_FILE_KEY = "kinecosystem_settings_data_source";

	private final SharedPreferences settingsDataSourceSharedPref;

	public SettingsDataSourceLocal(@NonNull final Context context) {
		this.settingsDataSourceSharedPref = context.getSharedPreferences(SETTINGS_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE);
	}

	@Override
	public void setIsBackedUp(final String publicAddress, boolean isBackedUp) {
		settingsDataSourceSharedPref.edit().putBoolean(publicAddress, isBackedUp).apply();
	}

	@Override
	public boolean isBackedUp(final String publicAddress) {
		return settingsDataSourceSharedPref.getBoolean(publicAddress, false);
	}
}
