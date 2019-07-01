package com.kin.ecosystem.gifting

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.kin.ecosystem.JwtProvider
import com.kin.ecosystem.common.KinTheme
import com.kin.ecosystem.common.ObservableData
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.common.model.OrderConfirmation
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.APageViewed
import com.kin.ecosystem.core.bi.events.GiftingButtonTapped
import com.kin.ecosystem.core.bi.events.GiftingFlowCompleted
import com.kin.ecosystem.core.bi.events.PageCloseTapped
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.internal.Configuration
import com.kin.ecosystem.core.data.order.OrderDataSource
import org.kin.ecosystem.appreciation.options.menu.ui.CloseType
import org.kin.ecosystem.appreciation.options.menu.ui.DialogTheme
import org.kin.ecosystem.appreciation.options.menu.ui.EventsListener
import org.kin.ecosystem.appreciation.options.menu.ui.GiftingDialog

internal class GiftingManagerImpl(private val jwtProvider: JwtProvider,
                                  private val blockchainSource: BlockchainSource,
                                  private val orderDataSource: OrderDataSource,
                                  private val eventLogger: EventLogger,
                                  private val configuration: Configuration) : GiftingManager, EventsListener {

	private val orderConfirmation: ObservableData<OrderConfirmation> = ObservableData.create()
	private var currentRecipientUserID: String? = null
	private var isShowing = false
	private val mainHandler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message?) {
			super.handleMessage(msg)
			when (msg?.what) {
				SendGiftCall.ORDER_SUCCEED -> {
					val confirmation = msg.obj as OrderConfirmation
					orderConfirmation.postValue(confirmation)
				}

				SendGiftCall.ORDER_FAILED -> {
					val kinException = msg.obj as KinEcosystemException
					OrderConfirmation().apply {
						exception = kinException
						status = OrderConfirmation.Status.FAILED
						orderConfirmation.postValue(this)
					}
				}
			}
		}
	}

	override fun addOrderConfirmationObserver(observer: Observer<OrderConfirmation>) = orderConfirmation.subscribe(observer)!!

	override fun showDialog(context: Context, recipientUserID: String) {
		if (isShowing) return
		isShowing = true
		currentRecipientUserID = recipientUserID
		GiftingDialog.Builder(context)
				.balance(getBalance())
				.eventsListener(this)
				.theme(getDialogTheme())
				.build()
				.show()
	}

	override fun onDialogClosed(closeType: CloseType) {
		isShowing = false
		when (closeType) {
			CloseType.CLOSE_BUTTON -> eventLogger.send(PageCloseTapped.create(PageCloseTapped.ExitType.X_BUTTON, PageCloseTapped.PageName.GIFTING_DIALOG))
			CloseType.TOUCH_OUTSIDE -> eventLogger.send(PageCloseTapped.create(PageCloseTapped.ExitType.BACKGROUND_APP, PageCloseTapped.PageName.GIFTING_DIALOG))
			CloseType.BACK_NAV_BUTTON -> eventLogger.send(PageCloseTapped.create(PageCloseTapped.ExitType.ANDROID_NAVIGATOR, PageCloseTapped.PageName.GIFTING_DIALOG))
			CloseType.ITEM_SELECTED -> eventLogger.send(GiftingFlowCompleted.create())
		}
	}

	override fun onDialogOpened() {
		eventLogger.send(APageViewed.create(APageViewed.PageName.GIFTING_DIALOG))
	}

	override fun onItemSelected(itemIndex: Int, amount: Int) {
		eventLogger.send(GiftingButtonTapped.create(amount.toDouble()))
		currentRecipientUserID?.let { recipient ->
			SendGiftCall(mainHandler, orderDataSource, jwtProvider, recipient, amount).apply {
				mainHandler.post(this)
			}
		}
	}

	private fun getBalance() = blockchainSource.balance.amount.toLong()

	private fun getDialogTheme() = if (configuration.kinTheme == KinTheme.LIGHT) DialogTheme.LIGHT else DialogTheme.DARK
}