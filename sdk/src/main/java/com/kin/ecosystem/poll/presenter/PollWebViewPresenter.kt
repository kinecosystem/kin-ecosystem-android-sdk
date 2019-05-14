package com.kin.ecosystem.poll.presenter

import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.*
import com.kin.ecosystem.core.bi.events.EarnOrderCompleted.OfferType
import com.kin.ecosystem.core.bi.events.EarnOrderFailed.Origin
import com.kin.ecosystem.core.data.internal.Configuration
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.network.model.OpenOrder
import com.kin.ecosystem.core.network.model.Order
import com.kin.ecosystem.poll.view.IPollWebView
import com.kin.ecosystem.poll.view.IPollWebView.Message

class PollWebViewPresenter(private val pollJsonString: String, private val offerID: String,
                           private val contentType: String, private val amount: Int, private val configuration: Configuration,
                           private val orderRepository: OrderDataSource, private val eventLogger: EventLogger) : BasePresenter<IPollWebView>(), IPollWebViewPresenter {

    private var openOrderObserver: Observer<OpenOrder>? = null
    private var openOrder: OpenOrder? = null
    private var isOrderSubmitted = false

    private val orderId: String
        get() = if (openOrder != null) openOrder!!.id else "null"

    override fun onAttach(view: IPollWebView) {
        super.onAttach(view)
        initWebView()
        listenToOpenOrders()
        createOrder()
    }

    private fun initWebView() {
        view?.initWebView()
    }

    private fun createOrder() {
        try {
            eventLogger.send(EarnOrderCreationRequested
                    .create(EarnOrderCreationRequested.OfferType.fromValue(contentType), amount.toDouble(), offerID, EarnOrderCreationRequested.Origin.MARKETPLACE))
        } catch (ex: IllegalArgumentException) {
            //TODO: add general error event
        }

        orderRepository.createOrder(offerID, object : KinCallback<OpenOrder> {
            override fun onResponse(response: OpenOrder?) {
                eventLogger.send(EarnOrderCreationReceived.create(offerID, response?.id, EarnOrderCreationReceived.Origin.MARKETPLACE))
                // we are listening to open orders.
            }

            override fun onFailure(exception: KinEcosystemException) {
                showToast(Message.SOMETHING_WENT_WRONG)
                val errorMsg = exception.message
                eventLogger.send(EarnOrderCreationFailed.create(errorMsg, offerID, EarnOrderCreationFailed.Origin.MARKETPLACE))
                closeView()
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        release()
    }

    private fun release() {
        if (openOrderObserver != null) {
            orderRepository.openOrder.removeObserver(openOrderObserver)
            openOrderObserver = null
        }
    }

    override fun onPageLoaded() {
        try {
            eventLogger.send(EarnPageLoaded.create(EarnPageLoaded.OfferType.fromValue(contentType)))

        } catch (ex: IllegalArgumentException) {
            //TODO: add general error event
        }

        view?.renderJson(pollJsonString, configuration.kinTheme!!.name)
    }


    override fun closeClicked() {
        eventLogger.send(CloseButtonOnOfferPageTapped.create(offerID, orderId))
        cancelOrderAndClose()
    }

    override fun onPageCancel() {
        cancelOrderAndClose()
    }

    private fun cancelOrderAndClose() {
        if (openOrder != null && !isOrderSubmitted) {
            val orderID = openOrder!!.id
            orderRepository.cancelOrder(offerID, orderID, null)
            eventLogger.send(EarnOrderCancelled.create(offerID, orderID))
        }
        closeView()
    }

    override fun onPageResult(result: String) {
        sendEarnOrderCompleted()
        if (openOrder != null) {
            isOrderSubmitted = true
            val orderId = openOrder!!.id
            eventLogger.send(EarnOrderCompletionSubmitted.create(offerID, orderId, EarnOrderCompletionSubmitted.Origin.MARKETPLACE))
            orderRepository.submitEarnOrder(offerID, result, orderId, object : KinCallback<Order> {
                override fun onResponse(response: Order) {}

                override fun onFailure(exception: KinEcosystemException) {
                    eventLogger.send(EarnOrderFailed.create(exception.cause?.message, offerID, orderId, Origin.MARKETPLACE))
                    showToast(Message.ORDER_SUBMISSION_FAILED)
                }
            })
        }
    }

    private fun sendEarnOrderCompleted() {
        try {
            eventLogger.send(
                    EarnOrderCompleted.create(OfferType.fromValue(contentType), amount.toDouble(), offerID, orderId, EarnOrderCompleted.Origin.MARKETPLACE))
        } catch (e: IllegalArgumentException) {
            //TODO: add general error event
        }

    }

    override fun onPageClosed() {
        closeView()
    }

    private fun listenToOpenOrders() {
        openOrderObserver = object : Observer<OpenOrder>() {
            override fun onChanged(value: OpenOrder) {
                openOrder = value
            }
        }
        orderRepository.openOrder.addObserver(openOrderObserver)
    }

    private fun showToast(msg: Message) {
        view?.showToast(msg)
    }

    private fun closeView() {
        view?.close()
    }
}
