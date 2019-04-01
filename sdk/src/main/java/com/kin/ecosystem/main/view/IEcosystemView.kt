package com.kin.ecosystem.main.view

import com.kin.ecosystem.base.IBaseView
import com.kin.ecosystem.main.INavigator

interface IEcosystemView : IBaseView, INavigator {

    override fun navigateBack()
}
