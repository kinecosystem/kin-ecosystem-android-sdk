package com.kin.ecosystem.history.view


import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextSwitcher
import android.widget.TextView
import com.kin.ecosystem.R
import com.kin.ecosystem.core.bi.EventLoggerImpl
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl
import com.kin.ecosystem.core.data.order.OrderRepository
import com.kin.ecosystem.core.data.settings.SettingsDataSourceImpl
import com.kin.ecosystem.core.data.settings.SettingsDataSourceLocal
import com.kin.ecosystem.core.network.model.Order
import com.kin.ecosystem.core.util.DeviceUtils
import com.kin.ecosystem.core.util.StringUtil.getAmountFormatted
import com.kin.ecosystem.history.presenter.IOrderHistoryPresenter
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.widget.KinEcosystemTabs
import com.kin.ecosystem.widget.TouchIndicatorIcon
import com.kin.ecosystem.widget.util.FontUtil
import com.kin.ecosystem.widget.util.ThemeUtil
import com.kin.ecosystem.withActions


open class OrderHistoryFragment : Fragment(), IOrderHistoryView {

    private var navigator: INavigator? = null
    private var orderHistoryPresenter: IOrderHistoryPresenter? = null

    private lateinit var earnRecyclerAdapter: OrderHistoryRecyclerAdapter
    private lateinit var spendRecyclerAdapter: OrderHistoryRecyclerAdapter
    private lateinit var earnOrderRecyclerView: RecyclerView
    private lateinit var spendOrderRecyclerView: RecyclerView
    private lateinit var settingsMenuIcon: TouchIndicatorIcon
    private lateinit var orderDescription: TextSwitcher
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.kinecosystem_fragment_order_history, container, false)
        initColors()
        initViews(root)
        orderHistoryPresenter = OrderHistoryPresenter(OrderRepository.getInstance(), BlockchainSourceImpl.getInstance(),
                SettingsDataSourceImpl(SettingsDataSourceLocal(context)), navigator, EventLoggerImpl.getInstance()).apply {
            onAttach(this@OrderHistoryFragment)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        orderHistoryPresenter?.onResume()
    }

    override fun onPause() {
        super.onPause()
        orderHistoryPresenter?.onPause()
    }

    override fun onDestroyView() {
        orderHistoryPresenter?.onDetach()
        earnOrderRecyclerView.removeAllViews()
        spendOrderRecyclerView.removeAllViews()
        navigator = null
        super.onDestroyView()
    }

    private fun initColors() {
        if (colorOrange == NOT_INITIALIZED) {
            colorOrange = ContextCompat.getColor(context, R.color.kinecosystem_orange)
        }
        if (colorPrimary == NOT_INITIALIZED) {
            colorPrimary = ThemeUtil.themeAttributeToColor(context, R.attr.primaryTextColor, R.color.kinecosystem_subtitle_order_history)
        }
        if (colorFailed == NOT_INITIALIZED) {
            colorFailed = ContextCompat.getColor(context, R.color.kinecosystem_failed)
        }
    }

    protected fun initViews(root: View) {
        orderDescription = root.findViewById<TextSwitcher>(R.id.order_description).apply {
            setFactory {
                val balanceText = TextView(context)
                balanceText.setTextAppearance(context, R.style.KinecosysSubTitle)
                balanceText.typeface = FontUtil.SAILEC
                balanceText.setTextColor(colorPrimary)
                balanceText
            }
        }

        root.findViewById<ImageView>(R.id.back_btn).apply {
            setOnClickListener { orderHistoryPresenter?.onBackButtonClicked() }
        }

        settingsMenuIcon = root.findViewById<TouchIndicatorIcon>(R.id.settings_icon).apply {
            setOnClickListener { orderHistoryPresenter?.onSettingsButtonClicked() }
        }

        //Earn Recycler
        earnOrderRecyclerView = root.findViewById(R.id.earn_order_recycler)
        earnRecyclerAdapter = OrderHistoryRecyclerAdapter().apply {
            bindToRecyclerView(earnOrderRecyclerView)
        }

        // Spend Recycler
        spendOrderRecyclerView = root.findViewById<RecyclerView>(R.id.spend_order_recycler).apply {
            x = DeviceUtils.getScreenWidth().toFloat()
            visibility = GONE
        }

        spendRecyclerAdapter = OrderHistoryRecyclerAdapter().apply {
            bindToRecyclerView(spendOrderRecyclerView)
        }

        root.findViewById<KinEcosystemTabs>(R.id.order_history_tabs).apply {
            setOnTabClickedListener { orderHistoryPresenter?.onTabSelected(it) }
        }
    }

    override fun updateSubTitle(amount: Int, orderStatus: OrderHistoryPresenter.OrderStatus, orderType: OrderHistoryPresenter.OrderType) {
        mainHandler.post {
            if(!isDetached) {
                val color: Int
                val subtitle: String
                val nextTextView = orderDescription.nextView as TextView
                when (orderStatus) {
                    OrderHistoryPresenter.OrderStatus.DELAYED -> {
                        subtitle = resources.getString(R.string.kinecosystem_sorry_this_may_take_some_time)
                        color = colorOrange
                    }
                    OrderHistoryPresenter.OrderStatus.COMPLETED -> {
                        subtitle = resources.getString(R.string.kinecosystem_earn_completed, getAmountFormatted(amount))
                        color = colorPrimary
                    }
                    OrderHistoryPresenter.OrderStatus.FAILED -> {
                        subtitle = resources.getString(R.string.kinecosystem_transaction_failed)
                        color = colorFailed
                    }
                    OrderHistoryPresenter.OrderStatus.PENDING -> {
                        subtitle = resources.getString(R.string.kinecosystem_earn_pending, getAmountFormatted(amount))
                        color = colorPrimary
                    }
                }

                nextTextView.setTextColor(color)
                nextTextView.text = subtitle
                orderDescription.showNext()
            }
        }
    }

    override fun showMenuTouchIndicator(visibility: Boolean) {
        settingsMenuIcon.setTouchIndicatorVisibility(visibility)
    }

    override fun showEarnList() {
        AnimatorSet().apply {
            val spendSlide = ValueAnimator.ofFloat(0F, DeviceUtils.getScreenWidth().toFloat()).apply {
                addUpdateListener {
                    spendOrderRecyclerView.x = it.animatedValue as Float
                }
                withActions(startAction = {
                    earnOrderRecyclerView.visibility = VISIBLE
                }, endAction = {
                    spendOrderRecyclerView.visibility = GONE
                })
            }
            val earnAnimSlide = ValueAnimator.ofFloat(-DeviceUtils.getScreenWidth().toFloat(), 0F).apply {
                addUpdateListener {
                    earnOrderRecyclerView.x = it.animatedValue as Float
                }
            }
            duration = DURATION_SLIDE_ANIM
            playTogether(spendSlide, earnAnimSlide)
            start()
        }
    }


    override fun showSpendList() {
        AnimatorSet().apply {
            val spendSlide = ValueAnimator.ofFloat(DeviceUtils.getScreenWidth().toFloat(), 0F ).apply {
                addUpdateListener {
                    spendOrderRecyclerView.x = it.animatedValue as Float
                }
                withActions(startAction = {
                    spendOrderRecyclerView.visibility = VISIBLE
                }, endAction = {
                    earnOrderRecyclerView.visibility = GONE
                })
            }
            val earnAnimSlide = ValueAnimator.ofFloat(0F, -DeviceUtils.getScreenWidth().toFloat()).apply {
                addUpdateListener {
                    earnOrderRecyclerView.x = it.animatedValue as Float
                }
            }
            duration = DURATION_SLIDE_ANIM
            playTogether(spendSlide, earnAnimSlide)
            start()
        }
    }

    override fun setEarnList(earnList: List<Order>) {
        earnRecyclerAdapter.let {
            it.setNewData(earnList)
            it.notifyDataSetChanged()
        }
    }

    override fun setSpendList(spendList: List<Order>) {
        spendRecyclerAdapter.let {
            it.setNewData(spendList)
            it.notifyDataSetChanged()
        }
    }

    override fun onEarnItemInserted() {
        earnRecyclerAdapter.notifyItemInserted(0)
    }

    override fun onSpendItemInserted() {
        spendRecyclerAdapter.notifyItemInserted(0)
    }

    override fun onEarnItemUpdated(index: Int) {
        earnRecyclerAdapter.notifyItemChanged(index)
    }

    override fun onSpendItemUpdated(index: Int) {
        spendRecyclerAdapter.notifyItemChanged(index)
    }

    override fun notifyEarnDataChanged() {
        earnRecyclerAdapter.notifyDataSetChanged()
    }

    override fun notifySpendDataChanged() {
        spendRecyclerAdapter.notifyDataSetChanged()
    }

    override fun setNavigator(navigator: INavigator) {
        this.navigator = navigator
    }

    companion object {
        private const val NOT_INITIALIZED = -1

        private const val DURATION_SLIDE_ANIM = 300L

        private var colorOrange = NOT_INITIALIZED
        private var colorPrimary = NOT_INITIALIZED
        private var colorFailed = NOT_INITIALIZED

        fun newInstance(navigator: INavigator): OrderHistoryFragment {
            val orderHistoryFragment = OrderHistoryFragment()
            orderHistoryFragment.navigator = navigator
            return orderHistoryFragment
        }
    }
}
