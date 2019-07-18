package com.kin.ecosystem.gifting

import android.os.Handler
import android.os.Message
import com.kin.ecosystem.JwtProvider
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.exception.ClientException
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.common.model.OrderConfirmation
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.util.ErrorUtil

internal class SendGiftCall(private val handler: Handler,
                   private val orderRepository: OrderDataSource,
                   private val jwtProvider: JwtProvider,
                   private val recipientUserID: String,
                   private val amount: Int): Runnable {

	override fun run() {
		val payToUserJwt = jwtProvider.getPayToUserJwt(recipientUserID, amount.toDouble())
		if (payToUserJwt.isNullOrEmpty()) {
			Message().apply {
				what = ORDER_FAILED
				obj = ErrorUtil.getClientException(ClientException.WRONG_JWT, null)
				handler.handleMessage(this)
			}
		} else {
			orderRepository.purchase(payToUserJwt, object: KinCallback<OrderConfirmation> {
				override fun onResponse(response: OrderConfirmation) {
					Message().apply {
						what = ORDER_SUCCEED
						obj = response
						handler.handleMessage(this)
					}
				}

				override fun onFailure(exception: KinEcosystemException?) {
					Message().apply {
						what = ORDER_FAILED
						obj = exception
						handler.handleMessage(this)
					}
				}
			})
		}
	}

	companion object {
		internal const val ORDER_SUCCEED = 1
		internal const val ORDER_FAILED = 2
	}
}