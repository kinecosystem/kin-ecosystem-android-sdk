package com.kin.ecosystem.core.data.settings

import android.content.Context
import android.content.SharedPreferences

class SettingsDataSourceLocal(context: Context) : SettingsDataSource.Local {

    private val settingsDataSourceSharedPref: SharedPreferences

    init {
        this.settingsDataSourceSharedPref = context.getSharedPreferences(SETTINGS_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE)
    }

    override fun setIsBackedUp(publicAddress: String, isBackedUp: Boolean) {
        settingsDataSourceSharedPref.edit().putBoolean(IS_BACKED_UP_KEY + publicAddress, isBackedUp).apply()
    }

    override fun isBackedUp(publicAddress: String): Boolean {
        return settingsDataSourceSharedPref.getBoolean(IS_BACKED_UP_KEY + publicAddress, false)
    }

    override fun setSawOnboarding(kinUserId: String) {
        settingsDataSourceSharedPref.edit().putBoolean(kinUserId, true).apply()
    }

    override fun isSawOnboarding(kinUserId: String): Boolean {
        return settingsDataSourceSharedPref.getBoolean(kinUserId, false)
    }

    companion object {

        private const val SETTINGS_PREF_NAME_FILE_KEY = "kinecosystem_settings_data_source"

        private const val IS_BACKED_UP_KEY = "backed_up_key"
    }
}
