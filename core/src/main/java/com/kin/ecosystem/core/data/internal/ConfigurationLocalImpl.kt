package com.kin.ecosystem.core.data.internal

import android.content.Context
import android.content.SharedPreferences
import com.kin.ecosystem.common.KinTheme

class ConfigurationLocalImpl(val context: Context) : Configuration.Local {

    private val configurationSharedPref: SharedPreferences

    override var kinTheme: KinTheme?
        get() = KinTheme.valueOf(configurationSharedPref.getString(KIN_THEME_KEY, KinTheme.LIGHT.name))
        set(value) {
            with(configurationSharedPref.edit()) {
                kinTheme?.let { currentTheme ->
                    if(currentTheme != value ) {
                        value?.let { value ->
                            putString(KIN_THEME_KEY, value.name).apply()
                        }
                    }
                }
            }
        }

    init {
        this.configurationSharedPref = context.getSharedPreferences(CONFIGURATION_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE)
    }

    companion object {
        private const val CONFIGURATION_PREF_NAME_FILE_KEY = "kinecosystem_configuration_data_source"

        private const val KIN_THEME_KEY = "kin_theme_key"
    }

}