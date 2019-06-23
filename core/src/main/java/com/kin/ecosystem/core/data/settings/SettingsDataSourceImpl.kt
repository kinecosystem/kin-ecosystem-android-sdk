package com.kin.ecosystem.core.data.settings

class SettingsDataSourceImpl(private val local: SettingsDataSource.Local) : SettingsDataSource {
    override fun setIsBackedUp(publicAddress: String, isBackedUp: Boolean) {
        local.setIsBackedUp(publicAddress, isBackedUp)
    }

    override fun isBackedUp(publicAddress: String): Boolean {
        return local.isBackedUp(publicAddress)
    }

    override fun setSawOnboarding(kinUserId: String) {
        local.setSeenOnboarding(kinUserId)
    }

    override fun hasSeenOnboarding(kinUserId: String): Boolean {
        return local.hasSeenOnboarding(kinUserId)
    }

    override fun hasSeenTransfer(kinUserId: String): Boolean {
        return local.hasSeenTransfer(kinUserId)
    }

    override fun setSeenTransfer(kinUserId: String) {
        local.setSeenTransfer(kinUserId)
    }
}
