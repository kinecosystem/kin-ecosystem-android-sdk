package com.kin.ecosystem.onboarding.view

import com.kin.ecosystem.base.IBaseView
import com.kin.ecosystem.onboarding.presenter.OnboardingPresenter
import com.kin.ecosystem.onboarding.presenter.OnboardingPresenterImpl

interface IOnboardingView : IBaseView {

    fun animateLoading()

    fun stopLoading(reset: Boolean)

    fun showToast(message: OnboardingPresenter.Message)
}
