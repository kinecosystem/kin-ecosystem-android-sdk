package com.kin.ecosystem.core.data.settings

interface SettingsDataSource {

    fun setIsBackedUp(publicAddress: String, isBackedUp: Boolean)

    fun isBackedUp(publicAddress: String): Boolean

    fun setSawOnboarding(kinUserId: String)

    fun isSawOnboarding(kinUserId: String) : Boolean

    interface Local {

        fun setIsBackedUp(publicAddress: String, isBackedUp: Boolean)

        fun isBackedUp(publicAddress: String): Boolean

        fun setSawOnboarding(kinUserId: String)

        fun isSawOnboarding(kinUserId: String) : Boolean
    }
}
