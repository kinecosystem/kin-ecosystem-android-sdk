package com.kin.ecosystem.main

import com.kin.ecosystem.base.CustomAnimation

interface INavigator {

    fun navigateToOnboarding()

    fun navigateToMarketplace(customAnimation: CustomAnimation)

    fun navigateToOrderHistory(isFirstSpendOrder: Boolean, addToBackStack: Boolean = true)

    fun navigateToSettings()

    fun close()
}
