package com.kin.ecosystem.core.data.settings

class SettingsDataSourceImpl(private val local: SettingsDataSource.Local) : SettingsDataSource {

    override fun setIsBackedUp(publicAddress: String, isBackedUp: Boolean) {
        local.setIsBackedUp(publicAddress, isBackedUp)
    }

    override fun isBackedUp(publicAddress: String): Boolean {
        return local.isBackedUp(publicAddress)
    }

    override fun setSawOnboarding(kinUserId: String) {
        local.setSawOnboarding(kinUserId)
    }

    override fun isSawOnboarding(kinUserId: String): Boolean {
        return local.isSawOnboarding(kinUserId)
    }
}
