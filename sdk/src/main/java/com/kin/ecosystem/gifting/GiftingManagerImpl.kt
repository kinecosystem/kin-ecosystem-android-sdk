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
	private val mainHandler = object: Handler(Looper.getMainLooper()) {
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
//		eventLogger.send()
	}

	override fun onDialogOpened() {
//		eventLogger.send()
	}

	override fun onItemSelected(itemIndex: Int, amount: Int) {
		currentRecipientUserID?.let { recipient ->
			SendGiftCall(mainHandler, orderDataSource, jwtProvider, recipient, amount).apply {
				mainHandler.post(this)
			}
		}
	}

	private fun getBalance() = blockchainSource.balance.amount.toLong()

	private fun getDialogTheme() = if (configuration.kinTheme == KinTheme.LIGHT) DialogTheme.LIGHT else DialogTheme.DARK
}