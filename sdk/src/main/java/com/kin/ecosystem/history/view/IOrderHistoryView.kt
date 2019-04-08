package com.kin.ecosystem.history.view

import com.kin.ecosystem.base.IBaseView
import com.kin.ecosystem.core.network.model.Order
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter
import com.kin.ecosystem.main.INavigator

interface IOrderHistoryView : IBaseView {

    fun showEarnList()

    fun showSpendList()

    fun setEarnList(earnList: List<Order>)

    fun setSpendList(spendList: List<Order>)

    fun onEarnItemInserted()

    fun onSpendItemInserted()

    fun onEarnItemUpdated(index: Int)

    fun onSpendItemUpdated(index: Int)

    fun notifyEarnDataChanged(range: IntRange)

    fun notifySpendDataChanged(range: IntRange)

    fun setNavigator(navigator: INavigator)

    fun showMenuTouchIndicator(visibility: Boolean)

    fun updateSubTitle(amount: Int, orderStatus: OrderHistoryPresenter.OrderStatus, orderType: OrderHistoryPresenter.OrderType)
}
