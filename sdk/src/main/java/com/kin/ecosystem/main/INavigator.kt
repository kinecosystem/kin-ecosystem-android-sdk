package com.kin.ecosystem.main

import com.kin.ecosystem.base.CustomAnimation

interface INavigator {

    fun navigateToOnboarding()

    fun navigateToMarketplace(customAnimation: CustomAnimation = com.kin.ecosystem.base.customAnimation {  })

    fun navigateToOrderHistory(isFirstSpendOrder: Boolean, addToBackStack: Boolean = true)

    fun navigateToSettings()

    fun close()
}
