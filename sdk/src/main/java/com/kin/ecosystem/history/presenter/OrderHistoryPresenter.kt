package com.kin.ecosystem.history.presenter


import android.support.annotation.NonNull
import com.kin.ecosystem.base.BaseFragmentPresenter
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.APageViewed
import com.kin.ecosystem.core.bi.events.ContinueButtonTapped
import com.kin.ecosystem.core.bi.events.PageCloseTapped
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.core.network.model.Offer
import com.kin.ecosystem.core.network.model.Order
import com.kin.ecosystem.core.network.model.Order.Status
import com.kin.ecosystem.core.network.model.OrderList
import com.kin.ecosystem.core.util.StringUtil
import com.kin.ecosystem.history.view.IOrderHistoryView
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.widget.KinEcosystemTabs
import java.math.BigDecimal

class OrderHistoryPresenter(private val orderRepository: OrderDataSource,
                            private val blockchainSource: BlockchainSource,
                            private val settingsDataSource: SettingsDataSource,
                            private val authDataSource: AuthDataSource,
                            navigator: INavigator?,
                            private val eventLogger: EventLogger) : BaseFragmentPresenter<IOrderHistoryView>(navigator), IOrderHistoryPresenter {

    private var earnOrderList: MutableList<Order> = ArrayList()
    private var spendOrderList: MutableList<Order> = ArrayList()

    private var balanceObserver: Observer<Balance>? = null
    private var currentBalance: Balance = blockchainSource.balance
    private var publicAddress: String = blockchainSource.publicAddress

    private var currentPendingOrder: Order? = null
    private var completedOrderObserver: Observer<Order>? = null

    override fun onAttach(view: IOrderHistoryView) {
        super.onAttach(view)
        eventLogger.send(APageViewed.create(APageViewed.PageName.MY_KIN_PAGE))
        getCachedHistory()
        listenToOrders()
    }

    override fun onResume() {
        updateMenuSettingsIcon()
    }

    override fun onPause() {
        removeBalanceObserver()
    }

    override fun onDetach() {
        super.onDetach()
        completedOrderObserver?.let {
            orderRepository.removeOrderObserver(it)
            completedOrderObserver = null
        }
    }

    override fun onEnterTransitionEnded() {
        getOrderHistoryList()
    }

    private fun getCachedHistory() {
        val cachedOrderListObj = orderRepository.allCachedOrderHistory
        cachedOrderListObj?.let {
            syncNewOrders(it)
        }
        view?.setEarnList(earnOrderList)
        view?.setSpendList(spendOrderList)
    }

    private fun getOrderHistoryList() {
        orderRepository.getAllOrderHistory(object : KinCallback<OrderList> {
            override fun onResponse(orderHistoryList: OrderList) {
                syncNewOrders(orderHistoryList)
            }

            override fun onFailure(exception: KinEcosystemException) {

            }
        })
    }

    private fun syncNewOrders(newOrdersListObj: OrderList) {
        val (newEarnList,newSpendList ) = splitByType(newOrdersListObj.orders)
        addOrders(newEarnList, earnOrderList)
        addOrders(newSpendList, spendOrderList)
    }

    private fun addOrders(orders: List<Order>, currentOrders: MutableList<Order>) {
        if (orders.isNotEmpty()) {
            //the oldest order is the last one, so we'll go from the last and add the top
            //we will end with newest order at the top.
            for (i in orders.indices.reversed()) {
                val order = orders[i]
                val index = currentOrders.indexOf(order)
                if (index == NOT_FOUND) {
                    //add at top (ui orientation)
                    addOrder(order = order)
                }
            }
        }
    }

    private fun splitByType(list: List<Order>) : Pair<List<Order>, List<Order>>{
        return list.filter { it.status != Status.PENDING }
                .partition { isEarn(it) }
    }

    private fun isEarn(order: Order) = order.offerType == Offer.OfferType.EARN

    override fun onTabSelected(tab: KinEcosystemTabs.Tab) {
        when (tab) {
            KinEcosystemTabs.Tab.LEFT -> view?.showEarnList()
            KinEcosystemTabs.Tab.RIGHT -> view?.showSpendList()
        }
    }

    private fun listenToOrders() {
        completedOrderObserver = object : Observer<Order>() {
            override fun onChanged(order: Order) {
                order.status?.let {
                    when (it) {
                        Status.PENDING -> {
                            currentPendingOrder = order
                            updateSubTitle(order)
                        }
                        Status.COMPLETED, Status.FAILED -> {
                            if (isCurrentOrder(order)) {
                                updateSubTitle(order)
                            }
                            addOrderOrUpdate(order)
                            updateRestOfTheOrders(order)
                        }
                        Status.DELAYED -> if (isCurrentOrder(order)) {
                            updateSubTitle(order)
                        }
                    }
                }
            }
        }
        orderRepository.addOrderObserver(completedOrderObserver!!)
    }

    private fun isCurrentOrder(order: Order): Boolean {
        return currentPendingOrder?.let { it == order } ?: false
    }

    private fun getStatus(@NonNull order: Order): OrderStatus {
        return when (order.status!!) {
            Order.Status.COMPLETED -> OrderStatus.COMPLETED
            Order.Status.FAILED -> OrderStatus.FAILED
            Order.Status.DELAYED -> OrderStatus.DELAYED
            Order.Status.PENDING -> OrderStatus.PENDING
        }
    }

    private fun getType(offerType: Offer.OfferType): OrderType {
        return when (offerType) {
            Offer.OfferType.SPEND -> OrderType.SPEND
            Offer.OfferType.EARN -> OrderType.EARN
        }
    }

    private fun updateSubTitle(order: Order) {
        if (order.origin == Order.Origin.MARKETPLACE) {
            val status = getStatus(order)
            view?.updateSubTitle(order.amount, status, getType(order.offerType))
        }
    }

    private fun updateRestOfTheOrders(order: Order) {
        if (isEarn(order)) {
            if(earnOrderList.size > 1) {
                view?.notifyEarnDataChanged(IntRange(1, earnOrderList.size - 1))
            }
        } else {
            if(spendOrderList.size > 1) {
                view?.notifySpendDataChanged(IntRange(1, spendOrderList.size - 1))
            }
        }
    }

    private fun addOrderOrUpdate(order: Order) {
        val index = if (isEarn(order)) earnOrderList.indexOf(order) else spendOrderList.indexOf(order)
        if (index == NOT_FOUND) {
            addOrder(order = order)
        } else {
            updateOrder(index = index, order = order)
        }
    }

    private fun addOrder(index: Int = 0, order: Order) {
        if (isEarn(order)) {
            earnOrderList.add(index, order)
            view?.onEarnItemInserted()
        } else {
            spendOrderList.add(index, order)
            view?.onSpendItemInserted()
        }
    }

    private fun updateOrder(index: Int, order: Order) {
        if (isEarn(order)) {
            earnOrderList[index] = order
            view?.onEarnItemUpdated(index)
        } else {
            spendOrderList[index] = order
            view?.onSpendItemUpdated(index)
        }
    }

    override fun onBackButtonClicked() {
        eventLogger.send(PageCloseTapped.create(PageCloseTapped.ExitType.ANDROID_NAVIGATOR, PageCloseTapped.PageName.MY_KIN_PAGE))
        navigator?.navigateBack()
    }

    override fun onSettingsButtonClicked() {
        eventLogger.send(ContinueButtonTapped.create(ContinueButtonTapped.PageName.MY_KIN_PAGE,
                ContinueButtonTapped.PageContinue.MY_KIN_PAGE_CONTINUE_TO_SETTINGS, null))
        navigator?.navigateToSettings()
    }

    private fun addBalanceObserver() {
        removeBalanceObserver()
        balanceObserver = object : Observer<Balance>() {
            override fun onChanged(value: Balance) {
                currentBalance = value
                if (isGreaterThenZero(value)) {
                    updateMenuSettingsIcon()
                }
            }
        }
        blockchainSource.addBalanceObserver(balanceObserver!!, false)
    }

    private fun removeBalanceObserver() {
        balanceObserver?.let {
            blockchainSource.removeBalanceObserver(it, false)
            balanceObserver = null
        }
    }

    private fun isGreaterThenZero(value: Balance) = value.amount.compareTo(BigDecimal.ZERO) == 1

    private fun updateMenuSettingsIcon() {
        publicAddress.let {
            if (!StringUtil.isEmpty(it)) {
                if (!settingsDataSource.isBackedUp(it)) {
                    if (isGreaterThenZero(currentBalance)) {
                        view?.showMenuTouchIndicator(true)
                        removeBalanceObserver()
                    } else {
                        addBalanceObserver()
                        view?.showMenuTouchIndicator(!settingsDataSource.hasSeenTransfer(authDataSource.ecosystemUserID))
                    }
                } else {
                    view?.showMenuTouchIndicator(!settingsDataSource.hasSeenTransfer(authDataSource.ecosystemUserID))
                }
            }
        }
    }

    enum class OrderType {
        EARN,
        SPEND
    }

    enum class OrderStatus {
        PENDING,
        DELAYED,
        COMPLETED,
        FAILED
    }

    companion object {
        private const val NOT_FOUND = -1
    }
}
