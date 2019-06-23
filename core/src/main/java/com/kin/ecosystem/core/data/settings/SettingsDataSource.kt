package com.kin.ecosystem.core.data.settings

interface SettingsDataSource {

    fun setIsBackedUp(publicAddress: String, isBackedUp: Boolean)

    fun isBackedUp(publicAddress: String): Boolean

    fun hasSeenTransfer(kinUserId: String): Boolean

    fun setSeenTransfer(kinUserId: String)

    fun setSawOnboarding(kinUserId: String)

    fun hasSeenOnboarding(kinUserId: String) : Boolean

    interface Local {

        fun setIsBackedUp(publicAddress: String, isBackedUp: Boolean)

        fun isBackedUp(publicAddress: String): Boolean

        fun setSeenOnboarding(kinUserId: String)

        fun hasSeenOnboarding(kinUserId: String) : Boolean

        fun setSeenTransfer(kinUserId: String)

        fun hasSeenTransfer(kinUserId: String) : Boolean
    }
}
