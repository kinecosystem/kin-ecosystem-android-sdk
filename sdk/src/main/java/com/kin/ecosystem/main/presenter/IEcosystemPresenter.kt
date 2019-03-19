package com.kin.ecosystem.main.presenter

import android.os.Bundle
import com.kin.ecosystem.base.IBasePresenter
import com.kin.ecosystem.main.ScreenId
import com.kin.ecosystem.main.view.IEcosystemView

interface IEcosystemPresenter : IBasePresenter<IEcosystemView> {

    fun onStart()

    fun onStop()

    fun touchedOutside()

    fun backButtonPressed()

    fun visibleScreen(@ScreenId id: Int)

    fun settingsMenuClicked()

    fun onMenuInitialized()

    fun onSaveInstanceState(outState: Bundle)
}