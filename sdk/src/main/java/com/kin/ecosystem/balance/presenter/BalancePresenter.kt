package com.kin.ecosystem.balance.presenter

import com.kin.ecosystem.balance.view.IBalanceView
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.network.model.Order

class BalancePresenter(private val blockchainSource: BlockchainSource,
                       private val orderRepository: OrderDataSource) : BasePresenter<IBalanceView>(), IBalancePresenter {

    private var balanceObserver: Observer<Balance>? = null
    private var orderObserver: Observer<Order>? = null

    private var pendingOrderCount: Int = 0

    override fun onAttach(view: IBalanceView) {
        super.onAttach(view)
        addObservers()
    }

    private fun createBalanceObserver() {
        balanceObserver = object : Observer<Balance>() {
            override fun onChanged(balance: Balance) {
                updateBalance(balance)
                // Balance is being updated before Order COMPLETED
                if (pendingOrderCount < 2) {
                    stopLoadingAnimation()
                }
            }
        }
    }

    private fun updateBalance(balance: Balance) {
        val balanceValue = balance.amount.toInt()
        view?.updateBalance(balanceValue)
    }

    private fun createOrderObserver() {
        orderObserver = object : Observer<Order>() {
            override fun onChanged(order: Order) {
                when (order.status) {
                    Order.Status.PENDING -> {
                        incrementPendingCount()
                        if(pendingOrderCount == 1) {
                            startLoadingAnimation()
                        }
                    }
                    Order.Status.COMPLETED -> {
                        decrementPendingCount()
                    }
                    Order.Status.FAILED -> {
                        decrementPendingCount()
                        if (pendingOrderCount == 0) {
                            stopLoadingAnimation()
                        }
                    }
                }
            }
        }
    }

    private fun startLoadingAnimation() {
        view?.startLoadingAnimation()
    }

    private fun stopLoadingAnimation() {
        view?.stopLoadingAnimation()
    }

    private fun incrementPendingCount() {
        pendingOrderCount++
    }

    private fun decrementPendingCount() {
        if (pendingOrderCount > 0) {
            pendingOrderCount--
        }
    }

    override fun onDetach() {
        super.onDetach()
        removeObservers()
        balanceObserver = null
        orderObserver = null
    }

    private fun addObservers() {
        removeObservers()
        createObservers()
        orderObserver?.let { orderRepository.addOrderObserver(it) }
        balanceObserver?.let { blockchainSource.addBalanceObserver(it, true) }
    }

    private fun createObservers() {
        createBalanceObserver()
        createOrderObserver()
    }

    private fun removeObservers() {
        orderObserver?.let { orderRepository.removeOrderObserver(it) }
        balanceObserver?.let { blockchainSource.removeBalanceObserver(it, true) }
    }
}
