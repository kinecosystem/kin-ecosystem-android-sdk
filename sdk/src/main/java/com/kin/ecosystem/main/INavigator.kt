package com.kin.ecosystem.main

import com.kin.ecosystem.base.CustomAnimation

interface INavigator {

    fun showNotEnoughKin(withAnimation: Boolean = true)

    fun navigateToOnboarding()

    fun navigateToMarketplace(customAnimation: CustomAnimation = com.kin.ecosystem.base.customAnimation {  })

    fun navigateToOrderHistory(customAnimation: CustomAnimation = com.kin.ecosystem.base.customAnimation {  }, addToBackStack: Boolean = true)

    fun navigateToSettings()

    fun navigateBack()

    fun close()
}
