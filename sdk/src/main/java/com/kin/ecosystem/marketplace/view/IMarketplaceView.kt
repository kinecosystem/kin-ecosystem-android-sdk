package com.kin.ecosystem.marketplace.view

import com.kin.ecosystem.base.IBaseView
import com.kin.ecosystem.core.network.model.Offer
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle

interface IMarketplaceView : IBaseView {

    enum class Message {
        NOT_ENOUGH_KIN,
        SOMETHING_WENT_WRONG
    }

    enum class Title {
        DEFAULT,
        EMPTY_STATE
    }

    fun setOfferList(offerList: List<Offer>)

    fun setupEmptyItemView()

    fun showOfferActivity(pollBundle: PollBundle)

    fun showToast(msg: Message)

    fun notifyOfferItemRemoved(index: Int)

    fun notifyOfferItemInserted(index: Int)

    fun notifyOfferItemRangRemoved(fromIndex: Int, size: Int)

    fun showMenuTouchIndicator(isVisible: Boolean)

    fun updateTitle(title: Title)
}
