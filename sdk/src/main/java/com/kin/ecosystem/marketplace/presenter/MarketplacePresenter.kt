package com.kin.ecosystem.marketplace.presenter


import com.kin.ecosystem.R
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.base.customAnimation
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.NativeOfferClickEvent
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.Subscription
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.*
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.offer.OfferDataSource
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.core.network.model.Offer
import com.kin.ecosystem.core.network.model.Offer.ContentTypeEnum
import com.kin.ecosystem.core.network.model.OfferList
import com.kin.ecosystem.core.network.model.Order
import com.kin.ecosystem.core.util.OfferConverter
import com.kin.ecosystem.core.util.StringUtil
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.marketplace.view.IMarketplaceView
import com.kin.ecosystem.marketplace.view.IMarketplaceView.*
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle
import java.math.BigDecimal
import java.util.*

class MarketplacePresenter(private val offerRepository: OfferDataSource,
                           private val orderRepository: OrderDataSource,
                           private val blockchainSource: BlockchainSource,
                           private val settingsDataSource: SettingsDataSource,
                           private var navigator: INavigator?,
                           private val eventLogger: EventLogger) : BasePresenter<IMarketplaceView>(), IMarketplacePresenter {

    private var offerList: MutableList<Offer>? = null
    private var nativeOfferRemovedSubscription: Subscription<Offer>? = null

    private var orderObserver: Observer<Order>? = null
    private var isListsAdded: Boolean = false

    private var balanceObserver: Observer<Balance>? = null
    private var currentBalance: Balance = blockchainSource.balance
    private var publicAddress: String = blockchainSource.publicAddress

    private var lastClickTime = NOT_FOUND.toLong()
    private val isFastClicks: Boolean
        get() {
            if (lastClickTime == NOT_FOUND.toLong()) {
                lastClickTime = System.currentTimeMillis()
                return false
            }

            val now = System.currentTimeMillis()
            if (now - lastClickTime < CLICK_TIME_INTERVAL) {
                return true
            }
            lastClickTime = now
            return false
        }

    override fun onAttach(view: IMarketplaceView) {
        super.onAttach(view)
        getCachedOffers()
        getOffers()
        eventLogger.send(MarketplacePageViewed.create())
    }

    override fun onStart() {
        listenToRemovedNativeOffers()
        listenToOrders()
        updateMenuSettingsIcon()
    }

    private fun listenToRemovedNativeOffers() {
        nativeOfferRemovedSubscription?.remove()
        nativeOfferRemovedSubscription = offerRepository.addNativeOfferRemovedObserver(object : Observer<Offer>() {
            override fun onChanged(nativeOffer: Offer?) {
                nativeOffer?.id?.let { removeOfferFromList(it) }
            }
        })
    }

    override fun onStop() {
        orderObserver?.let {
            orderRepository.removeOrderObserver(it)
            orderObserver = null
        }
        removeBalanceObserver()
    }

    override fun onDetach() {
        super.onDetach()
        nativeOfferRemovedSubscription?.remove()
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

    private fun isGreaterThenZero(value: Balance) =  value.amount.compareTo(BigDecimal.ZERO) == 1

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

    private fun getCachedOffers() {
        val cachedOfferList = offerRepository.cachedOfferList
        setCachedOfferLists(cachedOfferList)
    }

    private fun hasOffers(offerList: OfferList?): Boolean {
        return offerList != null && offerList.offers != null
    }

    private fun setCachedOfferLists(cachedOfferList: OfferList) {
        offerList ?: run { offerList = ArrayList() }
        offerList?.addAll(cachedOfferList.offers)

        view?.let {
            it.setOfferList(offerList)
            isListsAdded = true
        }
    }

    private fun listenToOrders() {
        orderObserver?.let {
            orderRepository.removeOrderObserver(it)
        }

        orderObserver = object : Observer<Order>() {
            override fun onChanged(order: Order) {
                if (order.status == Order.Status.PENDING) {
                    removeOfferFromList(order.offerId)
                }
            }
        }.also {
            orderRepository.addOrderObserver(it)
        }
    }

    private fun removeOfferFromList(offerId: String) {
        offerList?.let { offerList ->
            for (i in offerList.indices) {
                val offer = offerList[i]
                if (offer.id == offerId) {
                    offerList.removeAt(i)
                    notifyOfferItemRemoved(i)
                    return
                }
            }
        }
    }

    private fun notifyOfferItemRemoved(index: Int) {
        view?.notifyOfferItemRemoved(index)
    }

    private fun notifyOfferItemInserted(index: Int) {
        view?.notifyOfferItemInserted(index)
    }

    override fun getOffers() {
        this.offerRepository.getOffers(object : KinCallback<OfferList> {
            override fun onResponse(offerList: OfferList) {
                setupEmptyItemView()
                syncOffers(offerList)
            }

            override fun onFailure(exception: KinEcosystemException) {
                setupEmptyItemView()
            }
        })
    }

    private fun setupEmptyItemView() {
        view?.setupEmptyItemView()
    }

    private fun syncOffers(offers: OfferList) {
        if (hasOffers(offers)) {
            offerList ?: kotlin.run { offerList = ArrayList() }
            offerList?.let {
                syncList(offers.offers, it)
            }
        }
    }

    private fun syncList(newList: List<Offer>, oldList: MutableList<Offer>) {
        // check if offer should be removed (index changed / removed from list).
        if (newList.isNotEmpty()) {
            val iterator = oldList.iterator()
            iterator.withIndex().forEach {
                val index = newList.indexOf(it.value)
                if (index == NOT_FOUND || index != it.index) {
                    iterator.remove()
                    notifyItemRemoved(it.index)
                }
            }

            // Add missing offers, the order matters
            for (i in newList.indices) {
                val offer = newList[i]
                if (i < oldList.size) {
                    if (oldList[i] != offer) {
                        oldList.add(i, offer)
                        notifyItemInserted(i)
                    }
                } else {
                    oldList.add(offer)
                    notifyItemInserted(i)
                }
            }
        } else {
            val size = oldList.size
            if (size > 0) {
                oldList.clear()
                notifyItemRangRemoved(0, size)
            }
        }
    }

    private fun notifyItemRangRemoved(fromIndex: Int, size: Int) {
        view?.notifyOfferItemRangRemoved(fromIndex, size)
    }

    private fun notifyItemRemoved(index: Int) {
        notifyOfferItemRemoved(index)
    }

    private fun notifyItemInserted(index: Int) {
        notifyOfferItemInserted(index)
    }

    override fun onItemClicked(position: Int) {
        if (isFastClicks) {
            return
        }

        if (position == NOT_FOUND) {
            return
        }

        offerList?.let {
            val offer = it[position]
            sendOfferTappedEvent(offer)

            if (offer.offerType == Offer.OfferType.SPEND) {
                val balance = blockchainSource.balance.amount.toInt()
                val amount = BigDecimal(offer.amount)

                if (balance < amount.toInt()) {
                    eventLogger.send(NotEnoughKinPageViewed.create())
                    showToast(NOT_ENOUGH_KIN)
                    return
                }
            }

            if (isExternalOffer(offer)) {
                val dismissOnTap = offerRepository.shouldDismissOnTap(offer.id)
                if (dismissOnTap) {
                    closeMarketplace()
                }
                onNativeOfferClicked(offer, dismissOnTap)
            } else {
                view?.let { view ->
                    val pollBundle = PollBundle()
                            .setJsonData(offer.content)
                            .setOfferID(offer.id)
                            .setContentType(offer.contentType.value)
                            .setAmount(offer.amount)
                            .setTitle(offer.title)
                    view.showOfferActivity(pollBundle)
                }
            }
        } ?: run {
            sendSdkError("MarketplacePresenter offerList is null, offer position is: $position, isListsAdded: $isListsAdded")
        }
    }

    private fun sendSdkError(msg: String) {
        eventLogger.send(GeneralEcosystemSdkError.create(msg))
    }

    private fun isExternalOffer(offer: Offer): Boolean {
        return offer.contentType == ContentTypeEnum.EXTERNAL
    }

    private fun closeMarketplace() {
        navigator?.close()
    }

    private fun sendOfferTappedEvent(offer: Offer) {
        val amount = offer.amount.toDouble()
        try {
            if (offer.offerType == Offer.OfferType.EARN) {
                val offerType = EarnOfferTapped.OfferType.fromValue(offer.contentType.value)
                val origin = if (isExternalOffer(offer)) EarnOfferTapped.Origin.EXTERNAL else EarnOfferTapped.Origin.MARKETPLACE
                eventLogger.send(EarnOfferTapped.create(offerType, amount, offer.id, origin))
            } else {
                val origin = if (isExternalOffer(offer)) SpendOfferTapped.Origin.EXTERNAL else SpendOfferTapped.Origin.MARKETPLACE
                eventLogger.send(SpendOfferTapped.create(amount, offer.id, origin))
            }
        } catch (ex: Exception) {
            sendSdkError("sendOfferTapped Event ${ex.message}")
        }
    }

    private fun onNativeOfferClicked(offer: Offer, dismissMarketplace: Boolean) {
        val nativeOffer = OfferConverter.toNativeOffer(offer)
        offerRepository.nativeSpendOfferObservable.postValue(
                NativeOfferClickEvent.Builder()
                        .nativeOffer(nativeOffer)
                        .isDismissed(dismissMarketplace)
                        .build())
    }

    private fun showSomethingWentWrong() {
        showToast(SOMETHING_WENT_WRONG)
    }

    override fun showOfferActivityFailed() {
        showSomethingWentWrong()
    }

    override fun backButtonPressed() {
        eventLogger.send(BackButtonOnMarketplacePageTapped.create())
    }

    override fun setNavigator(navigator: INavigator) {
        this.navigator = navigator
    }

    override fun closeClicked() {
        closeMarketplace()
    }

    override fun myKinCLicked() {
        navigator?.navigateToOrderHistory(customAnimation {
            enter = R.anim.kinecosystem_slide_in_right
            exit = R.anim.kinecosystem_slide_out_left
            popEnter = R.anim.kinrecovery_slide_in_left
            popExit = R.anim.kinecosystem_slide_out_right
        }, addToBackStack = true)
    }

    private fun showToast(@Message msg: Int) {
        view?.showToast(msg)
    }

    companion object {
        private const val NOT_FOUND = -1
        private const val CLICK_TIME_INTERVAL: Long = 350
    }
}
