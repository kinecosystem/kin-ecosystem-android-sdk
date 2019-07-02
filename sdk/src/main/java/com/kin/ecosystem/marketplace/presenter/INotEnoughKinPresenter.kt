package com.kin.ecosystem.marketplace.presenter

import com.kin.ecosystem.base.IBaseFragmentPresenter
import com.kin.ecosystem.marketplace.view.INotEnoughKinView

interface INotEnoughKinPresenter : IBaseFragmentPresenter<INotEnoughKinView> {

	fun onEarnMoreKinClicked()

	fun closeClicked()
}