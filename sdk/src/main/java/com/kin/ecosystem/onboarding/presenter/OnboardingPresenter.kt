package com.kin.ecosystem.onboarding.presenter

import com.kin.ecosystem.base.IBaseFragmentPresenter
import com.kin.ecosystem.base.IBasePresenter
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.onboarding.view.IOnboardingView

interface OnboardingPresenter : IBaseFragmentPresenter<IOnboardingView> {

    enum class Message {
        TRY_AGAIN,
        SOMETHING_WENT_WRONG
    }

    fun getStartedClicked()

    fun closeButtonPressed()
}
