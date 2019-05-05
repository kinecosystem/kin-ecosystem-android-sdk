package com.kin.ecosystem.marketplace.presenter

import com.kin.ecosystem.base.IBaseFragmentPresenter
import com.kin.ecosystem.base.IBasePresenter
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.marketplace.view.IMarketplaceView

interface IMarketplacePresenter : IBaseFragmentPresenter<IMarketplaceView> {

    fun onResume()

    fun onPause()

    fun getOffers()

    fun onItemClicked(position: Int)

    fun showOfferActivityFailed()

    fun closeClicked()

    fun myKinCLicked()
}
