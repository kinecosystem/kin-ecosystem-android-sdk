package com.kin.ecosystem.marketplace.view

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.kin.ecosystem.R
import com.kin.ecosystem.common.exception.ClientException
import com.kin.ecosystem.core.bi.EventLoggerImpl
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl
import com.kin.ecosystem.core.data.offer.OfferRepository
import com.kin.ecosystem.core.data.order.OrderRepository
import com.kin.ecosystem.core.data.settings.SettingsDataSourceImpl
import com.kin.ecosystem.core.data.settings.SettingsDataSourceLocal
import com.kin.ecosystem.core.network.model.Offer
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.marketplace.presenter.IMarketplacePresenter
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter
import com.kin.ecosystem.poll.view.PollWebViewActivity
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle
import com.kin.ecosystem.widget.TouchIndicatorIcon


class MarketplaceFragment : Fragment(), IMarketplaceView {

    private var navigator: INavigator? = null
    private var marketplacePresenter: IMarketplacePresenter? = null

    private lateinit var offersRecyclerAdapter: OfferRecyclerAdapter
    private lateinit var offersRecycler: RecyclerView
    private lateinit var myKinButton: TouchIndicatorIcon
    private lateinit var screenTitle: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.kinecosystem_fragment_marketplce, container, false)
        initViews(root)
        marketplacePresenter = MarketplacePresenter(OfferRepository.getInstance(),
                OrderRepository.getInstance(),
                BlockchainSourceImpl.getInstance(),
                SettingsDataSourceImpl(SettingsDataSourceLocal(context)),
                navigator,
                EventLoggerImpl.getInstance())
        marketplacePresenter?.onAttach(this)
        return root
    }

    override fun onResume() {
        super.onResume()
        marketplacePresenter?.onResume()
    }

    override fun onPause() {
        super.onPause()
        marketplacePresenter?.onPause()
    }

    override fun onDestroyView() {
        marketplacePresenter?.onDetach()
        offersRecycler.removeAllViews()
        navigator = null
        super.onDestroyView()
    }

    private fun initViews(root: View) {
        screenTitle = root.findViewById(R.id.title)
        root.findViewById<ImageView>(R.id.close_btn).apply {
            setOnClickListener { marketplacePresenter?.closeClicked() }
        }
        myKinButton = root.findViewById<TouchIndicatorIcon>(R.id.my_kin_btn).apply {
            setOnClickListener { marketplacePresenter?.myKinCLicked() }
        }

        //Space item decoration for both of the recyclers
        val margin = resources.getDimensionPixelOffset(R.dimen.kinecosystem_tiny_margin)
        val itemDecoration = SpaceItemDecoration(margin)

        offersRecycler = root.findViewById<RecyclerView>(R.id.offers_recycler).apply {
            addItemDecoration(itemDecoration)
        }
        offersRecyclerAdapter = OfferRecyclerAdapter().apply {
            bindToRecyclerView(offersRecycler)
            setOnItemClickListener { _, _, position -> marketplacePresenter?.onItemClicked(position) }
        }
    }

    override fun showOfferActivity(pollBundle: PollBundle) {
        try {
            val intent = PollWebViewActivity.createIntent(context, pollBundle)
            startActivity(intent)
            activity.overridePendingTransition(R.anim.kinecosystem_slide_in_right, R.anim.kinecosystem_slide_out_left)
        } catch (e: ClientException) {
            marketplacePresenter?.showOfferActivityFailed()
        }
    }

    override fun updateTitle(title: IMarketplaceView.Title) {
        screenTitle.setText(getTitleResId(title))
    }

    override fun showToast(msg: IMarketplaceView.Message) {
        Toast.makeText(context, getMessageResId(msg), Toast.LENGTH_SHORT).show()
    }

    @StringRes
    private fun getMessageResId(msg: IMarketplaceView.Message): Int {
        return when (msg) {
            IMarketplaceView.Message.NOT_ENOUGH_KIN -> R.string.kinecosystem_you_dont_have_enough_kin
            IMarketplaceView.Message.SOMETHING_WENT_WRONG -> R.string.kinecosystem_something_went_wrong
        }
    }

    @StringRes
    private fun getTitleResId(title: IMarketplaceView.Title): Int {
        return when (title) {
            IMarketplaceView.Title.DEFAULT -> R.string.kinecosystem_what_are_you_in_the_mood_for
            IMarketplaceView.Title.EMPTY_STATE -> R.string.kinecosystem_well_done_check_back_soon
        }
    }

    override fun setOfferList(offerList: List<Offer>) {
        offersRecyclerAdapter.setNewData(offerList)
    }

    override fun setupEmptyItemView() {
        offersRecyclerAdapter.setEmptyView(OffersEmptyView(context))
    }

    override fun notifyOfferItemRemoved(index: Int) {
        offersRecyclerAdapter.notifyItemRemoved(index)
    }

    override fun notifyOfferItemInserted(index: Int) {
        offersRecyclerAdapter.notifyItemInserted(index)
        offersRecycler.scheduleLayoutAnimation()
    }

    override fun notifyOfferItemRangRemoved(fromIndex: Int, size: Int) {
        offersRecyclerAdapter.notifyItemRangeRemoved(fromIndex, size)
    }

    override fun showMenuTouchIndicator(isVisible: Boolean) {
        myKinButton.setTouchIndicatorVisibility(isVisible)
    }

    fun setNavigator(nav: INavigator) {
        navigator = nav
        marketplacePresenter?.setNavigator(navigator)
    }

    companion object {
        fun newInstance(navigator: INavigator): MarketplaceFragment {
            val marketplaceFragment = MarketplaceFragment()
            marketplaceFragment.navigator = navigator
            return marketplaceFragment
        }
    }
}
