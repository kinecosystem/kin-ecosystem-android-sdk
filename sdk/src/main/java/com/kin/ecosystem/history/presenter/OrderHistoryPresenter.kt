package com.kin.ecosystem.history.presenter


import android.support.annotation.NonNull
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.OrderHistoryPageViewed
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
                            private var navigator: INavigator?,
                            private val eventLogger: EventLogger) : BasePresenter<IOrderHistoryView>(), IOrderHistoryPresenter {

    private var earnOrderList: MutableList<Order> = ArrayList()
    private var spendOrderList: MutableList<Order> = ArrayList()

    private var balanceObserver: Observer<Balance>? = null
    private var currentBalance: Balance = blockchainSource.balance
    private var publicAddress: String = blockchainSource.publicAddress

    private var currentPendingOrder: Order? = null
    private var completedOrderObserver: Observer<Order>? = null

    override fun onAttach(view: IOrderHistoryView) {
        super.onAttach(view)
        eventLogger.send(OrderHistoryPageViewed.create())
        getOrderHistoryList()
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

    private fun getOrderHistoryList() {
        val cachedOrderListObj = orderRepository.allCachedOrderHistory
        cachedOrderListObj?.let {
            syncNewOrders(it)
        }
        view?.setEarnList(earnOrderList)
        view?.setSpendList(spendOrderList)
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
                } else {
                    //Update
                    updateOrder(index = index, order = order)
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
                if (order.origin == Order.Origin.MARKETPLACE) {
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
                                notifyDataChanged(order)
                            }
                            Status.DELAYED -> if (isCurrentOrder(order)) {
                                updateSubTitle(order)
                            }
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
        val status = getStatus(order)
        view?.updateSubTitle(order.amount, status, getType(order.offerType))
    }

    private fun notifyDataChanged(order: Order) {
        if (isEarn(order)) {
            view?.notifyEarnDataChanged()
        } else {
            view?.notifySpendDataChanged()
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
        navigator?.navigateBack()
    }

    override fun onSettingsButtonClicked() {
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
                        view?.showMenuTouchIndicator(false)
                    }
                } else {
                    view?.showMenuTouchIndicator(false)
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
