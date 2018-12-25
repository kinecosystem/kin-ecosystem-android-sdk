package com.kin.ecosystem.marketplace.presenter


import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.NativeOfferClickEvent
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.*
import com.kin.ecosystem.core.bi.events.SpendOfferTapped.Origin
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.offer.OfferDataSource
import com.kin.ecosystem.core.data.offer.OfferListUtil
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.network.model.Offer
import com.kin.ecosystem.core.network.model.Offer.ContentTypeEnum
import com.kin.ecosystem.core.network.model.Offer.OfferType
import com.kin.ecosystem.core.network.model.OfferInfo
import com.kin.ecosystem.core.network.model.OfferList
import com.kin.ecosystem.core.network.model.Order
import com.kin.ecosystem.core.util.OfferConverter
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.marketplace.view.IMarketplaceView
import com.kin.ecosystem.marketplace.view.IMarketplaceView.*
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle
import java.math.BigDecimal
import java.util.*

class MarketplacePresenter(view: IMarketplaceView, private val offerRepository: OfferDataSource,
                           private val orderRepository: OrderDataSource, private val blockchainSource: BlockchainSource?,
                           private var navigator: INavigator?, private val eventLogger: EventLogger) : BasePresenter<IMarketplaceView>(), IMarketplacePresenter {

    private var spendList: MutableList<Offer>? = null
    private var earnList: MutableList<Offer>? = null

    private var orderObserver: Observer<Order>? = null


    private var lastClickTime = NOT_FOUND.toLong()
    private val gson: Gson

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

    init {
        this.view = view
        this.gson = Gson()
        this.view.attachPresenter(this)
    }

    override fun onAttach(view: IMarketplaceView) {
        super.onAttach(view)
        eventLogger.send(MarketplacePageViewed.create())
    }

    override fun onDetach() {
        super.onDetach()
        navigator = null
    }

    override fun onStart() {
        getCachedOffers()
        getOffers()
        listenToOrders()
    }

    override fun onStop() {
        if (orderObserver != null) {
            orderRepository.removeOrderObserver(orderObserver!!)
            orderObserver = null
        }
        earnList = null
        spendList = null
    }

    private fun getCachedOffers() {
        val cachedOfferList = offerRepository.cachedOfferList
        setCachedOfferLists(cachedOfferList)
    }

    private fun hasOffers(offerList: OfferList?): Boolean {
        return !(offerList == null || offerList.offers == null)
    }

    private fun setCachedOfferLists(cachedOfferList: OfferList) {
        if (earnList == null && spendList == null) {
            earnList = ArrayList()
            spendList = ArrayList()
        }

        if (hasOffers(cachedOfferList)) {
            OfferListUtil.splitOffersByType(cachedOfferList.offers, earnList, spendList)
        }

        if (this.view != null) {
            this.view.setEarnList(earnList)
            this.view.setSpendList(spendList)

            if (!earnList!!.isEmpty()) {
                updateEarnTitle()
            }
            if (!spendList!!.isEmpty()) {
                updateSpendTitle()
            }
        }
    }

    private fun listenToOrders() {
        orderObserver?.let {
            orderRepository.removeOrderObserver(it)
        }
        orderObserver = object : Observer<Order>() {
            override fun onChanged(order: Order) {
                when (order.status) {
                    Order.Status.PENDING -> removeOfferFromList(order.offerId, order.offerType)
                    Order.Status.FAILED, Order.Status.COMPLETED -> getOffers()
                    else -> {
                        // no-op
                    }
                }

            }
        }
        orderRepository.addOrderObserver(orderObserver!!)
    }

    private fun removeOfferFromList(offerId: String, offerType: OfferType) {
        if (offerType == OfferType.EARN) {
            earnList?.let {
                for (i in it.indices) {
                    val offer = it[i]
                    if (offer.id == offerId) {
                        it.removeAt(i)
                        notifyEarnItemRemoved(i)
                        updateEarnTitle()
                        return
                    }
                }
            }
        } else {
            spendList?.let {
                for (i in it.indices) {
                    val offer = it[i]
                    if (offer.id == offerId) {
                        it.removeAt(i)
                        notifySpendItemRemoved(i)
                        updateSpendTitle()
                        return
                    }
                }
            }
        }
    }

    private fun updateEarnTitle() {
        if (view != null && earnList != null) {
            val isEarnListEmpty = earnList!!.isEmpty()
            view.updateEarnSubtitle(isEarnListEmpty)
        }
    }

    private fun updateSpendTitle() {
        if (view != null && spendList != null) {
            val isSpendListEmpty = spendList!!.isEmpty()
            view.updateSpendSubtitle(isSpendListEmpty)
        }
    }

    private fun notifyEarnItemRemoved(index: Int) {
        view?.notifyEarnItemRemoved(index)
    }

    private fun notifyEarnItemInserted(index: Int) {
        view?.notifyEarnItemInserted(index)

    }

    private fun notifySpendItemRemoved(index: Int) {
        view?.notifySpendItemRemoved(index)
    }

    private fun notifySpendItemInserted(index: Int) {
        view?.notifySpendItemInserted(index)
    }

    override fun getOffers() {
        offerRepository.getOffers(object : KinCallback<OfferList> {
            override fun onResponse(offerList: OfferList) {
                setupEmptyItemView()
                syncOffers(offerList)
            }

            override fun onFailure(exception: KinEcosystemException) {
                setupEmptyItemView()
                updateEarnTitle()
                updateSpendTitle()
            }
        })
    }

    private fun setupEmptyItemView() {
        view?.setupEmptyItemView()
    }

    private fun syncOffers(offerList: OfferList) {
        if (hasOffers(offerList)) {
            val newEarnOffers = ArrayList<Offer>()
            val newSpendOffers = ArrayList<Offer>()

            OfferListUtil.splitOffersByType(offerList.offers, newEarnOffers, newSpendOffers)

            
            earnList ?: run {
                earnList = ArrayList()
            }
            earnList ?: run {
                spendList = ArrayList()
            }
            syncList(newEarnOffers, earnList!!, OfferType.EARN)
            syncList(newSpendOffers, spendList!!, OfferType.SPEND)
        }
    }

    private fun syncList(newList: List<Offer>, oldList: MutableList<Offer>, offerType: OfferType) {
        // check if offer should be removed (index changed / removed from list).
        if (newList.isNotEmpty()) {
            val iterator = oldList.iterator()
            iterator.withIndex().forEach {
                val index = newList.indexOf(it.value)
                if (index == NOT_FOUND || index != it.index) {
                    iterator.remove()
                    notifyItemRemoved(it.index, offerType)
                }
            }

            // Add missing offers, the order matters
            for (i in newList.indices) {
                val offer = newList[i]
                if (i < oldList.size) {
                    if (oldList[i] != offer) {
                        oldList.add(i, offer)
                        notifyItemInserted(i, offerType)
                    }
                } else {
                    oldList.add(offer)
                    notifyItemInserted(i, offerType)
                }
            }
        } else {
            val size = oldList.size
            if (size > 0) {
                oldList.clear()
                notifyItemRangRemoved(0, size, offerType)
            }
        }

        if (offerType == OfferType.EARN) {
            updateEarnTitle()
        } else {
            updateSpendTitle()
        }
    }

    private fun notifyItemRangRemoved(fromIndex: Int, size: Int, offerType: OfferType) {
        view?.let {
            if (isSpend(offerType)) {
                it.notifySpendItemRangRemoved(fromIndex, size)
            } else {
                it.notifyEarnItemRangRemoved(fromIndex, size)
            }
        }
    }

    private fun notifyItemRemoved(index: Int, offerType: OfferType) {
        if (isSpend(offerType)) {
            notifySpendItemRemoved(index)
        } else {
            notifyEarnItemRemoved(index)
        }
    }

    private fun notifyItemInserted(index: Int, offerType: OfferType) {
        if (isSpend(offerType)) {
            notifySpendItemInserted(index)
            updateSpendTitle()
        } else {
            notifyEarnItemInserted(index)
            updateEarnTitle()
        }
    }

    private fun isSpend(offerType: OfferType): Boolean {
        return offerType == OfferType.SPEND
    }

    override fun onItemClicked(position: Int, offerType: OfferType) {
        if (isFastClicks) {
            return
        }

        if (position == NOT_FOUND) {
            return
        }

        if (offerType == OfferType.EARN) {
            earnList?.let {
                val offer = it[position]
                sendEranOfferTapped(offer)
                if (onExternalItemClicked(offer)) {
                    return
                }
                view?.let { view ->
                    val pollBundle = PollBundle()
                            .setJsonData(offer.content)
                            .setOfferID(offer.id)
                            .setContentType(offer.contentType.value)
                            .setAmount(offer.amount!!)
                            .setTitle(offer.title)
                    view.showOfferActivity(pollBundle)
                }
            }

        } else {
            spendList?.let {
                val offer = it[position]
                sendSpendOfferTapped(offer)
                if (onExternalItemClicked(offer)) {
                    return
                }
                val balance = blockchainSource!!.balance.amount.toInt()
                val amount = BigDecimal(offer.amount!!)

                if (balance < amount.toInt()) {
                    eventLogger.send(NotEnoughKinPageViewed.create())
                    showToast(NOT_ENOUGH_KIN)
                    return
                }

                val offerInfo = deserializeOfferInfo(offer.content)
                if (offerInfo != null) {
                    showSpendDialog(offerInfo, offer)
                } else {
                    showSomethingWentWrong()
                }
            }
        }
    }

    private fun onExternalItemClicked(offer: Offer?): Boolean {
        if (offer != null && offer.contentType == ContentTypeEnum.EXTERNAL) {
            val dismissOnTap = offerRepository.shouldDismissOnTap(offer.id)
            if (dismissOnTap) {
                closeMarketplace()
            }
            nativeSpendOfferClicked(offer, dismissOnTap)
            return true
        }
        return false
    }

    private fun closeMarketplace() {
        navigator?.close()
    }

    private fun sendEranOfferTapped(offer: Offer) {
        val offerType: EarnOfferTapped.OfferType
        try {
            offerType = EarnOfferTapped.OfferType.fromValue(offer.contentType.value)
            val amount = offer.amount.toDouble()
            eventLogger.send(EarnOfferTapped.create(offerType, amount, offer.id))
        } catch (ex: IllegalArgumentException ) {
            //TODO: add general error event
        } catch (ex: NullPointerException) { }
    }

    private fun sendSpendOfferTapped(offer: Offer) {
        val amount = offer.amount.toDouble()
        val contentType = offer.contentType
        eventLogger.send(SpendOfferTapped.create(amount, offer.id, if (contentType == ContentTypeEnum.EXTERNAL) Origin.EXTERNAL else Origin.MARKETPLACE))
    }

    private fun nativeSpendOfferClicked(offer: Offer, dismissMarketplace: Boolean) {
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

    override fun getNavigator(): INavigator? {
        return navigator
    }

    override fun setNavigator(navigator: INavigator?) {
        this.navigator = navigator
    }

    private fun showSpendDialog(offerInfo: OfferInfo, offer: Offer) {
        view?.showSpendDialog(createSpendDialogPresenter(offerInfo, offer))
    }

    private fun createSpendDialogPresenter(offerInfo: OfferInfo, offer: Offer): ISpendDialogPresenter {
        return SpendDialogPresenter(offerInfo, offer, blockchainSource, orderRepository, eventLogger)
    }

    private fun deserializeOfferInfo(content: String): OfferInfo? {
        return try { gson.fromJson<OfferInfo>(content, OfferInfo::class.java) }
               catch (t: JsonSyntaxException) { null }
    }

    private fun showToast(@Message msg: Int) {
        view?.showToast(msg)
    }

    companion object {

        private const val NOT_FOUND = -1
        private const val CLICK_TIME_INTERVAL: Long = 350
    }
}
