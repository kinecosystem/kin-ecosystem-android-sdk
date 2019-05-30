package com.kin.ecosystem.core.data.order

import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.SpendOrderCompletionSubmitted
import com.kin.ecosystem.core.bi.events.SpendOrderCreationReceived
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.blockchain.Payment
import com.kin.ecosystem.core.network.model.BlockchainData
import com.kin.ecosystem.core.network.model.JWTBodyPaymentConfirmationResult
import com.kin.ecosystem.core.network.model.OpenOrder
import com.kin.ecosystem.core.network.model.Order
import com.nhaarman.mockitokotlin2.*
import kin.ecosystem.test.base.BaseTestClass
import kin.sdk.migration.common.KinSdkVersion
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ExternalSpendOrderCallTest : BaseTestClass() {

	private val orderDataSource: OrderDataSource = mock {
		on { createExternalOrderSync(any()) } doAnswer { openOrder}
	}
	private val blockchainSource: BlockchainSource = mock {
		on { blockchainVersion } doAnswer { KinSdkVersion.OLD_KIN_SDK }
		on { balance } doAnswer { balance }
	}

	private val balance: Balance = mock {
		on { amount } doAnswer { BigDecimal(5000) }
	}

	private val eventLogger: EventLogger = mock()
	private val externalOrderCallbacks: CreateExternalOrderCall.ExternalSpendOrderCallbacks = mock()

	private val openOrder: OpenOrder = mock {
		on { offerId } doAnswer  { offerId }
		on { id } doAnswer { orderId }
		on { amount } doAnswer { 10 }
		on { blockchainData } doAnswer { blockchainData }
	}

	private val blockchainData: BlockchainData = mock {
		on { recipientAddress } doAnswer { "some_address" }
	}

	private val order: Order = mock {
		on { orderId } doAnswer { orderId }
		on { offerId } doAnswer { offerId }
		on { result } doAnswer { jwtBodyPaymentConfirmationResult }
	}

	private val payment: Payment = mock {
		on { orderID } doAnswer { orderId }
		on { isSucceed } doAnswer { true }
		on { transactionID } doAnswer { transactionId }
		on { type } doAnswer { Payment.SPEND }
	}

	private val jwtBodyPaymentConfirmationResult: JWTBodyPaymentConfirmationResult = mock {
		on { jwt } doAnswer { "jwt_confirmation" }
	}


	private lateinit var externalEarnOrderCall: ExternalSpendOrderCall

	@Before
	override fun setUp() {
		super.setUp()
		externalEarnOrderCall = ExternalSpendOrderCall(orderDataSource, blockchainSource, orderJwt, eventLogger, externalOrderCallbacks)
	}

	@Test
	fun `run kin2 happy flow to completion`() {
		externalEarnOrderCall.run()
		val paymentCaptor = argumentCaptor<Observer<Payment>>()
		inOrder(orderDataSource, blockchainSource, eventLogger, externalOrderCallbacks).apply {
			verify(orderDataSource).createExternalOrderSync(orderJwt)
			verify(eventLogger).send(any<SpendOrderCreationReceived>())
			verify(blockchainSource).balance
			verify(blockchainSource).addPaymentObservable(paymentCaptor.capture())
			verify(blockchainSource).blockchainVersion
			argumentCaptor<KinCallback<Order>>().apply {
				verify(orderDataSource).submitSpendOrder(any(), isNull(), any(), capture())
				firstValue.onResponse(order)
			}

			verify(eventLogger).send(any<SpendOrderCompletionSubmitted>())
			verify(blockchainSource).sendTransaction(any(), any(), any(), any())
			paymentCaptor.firstValue.onChanged(payment)
			verify(blockchainSource).removePaymentObserver(any())
			argumentCaptor<KinCallback<Order>>().apply {
				verify(orderDataSource).getOrder(any(), capture())
				whenever(order.status).doReturn(Order.Status.COMPLETED)
				firstValue.onResponse(order)
			}
			verify(externalOrderCallbacks).onOrderConfirmed(jwtBodyPaymentConfirmationResult.jwt, order)
		}
	}

	companion object {
		private const val orderJwt = "eyJraWQiOiJyczUxMl8xIiwidHlwIjoiand0IiwiYWxnIjoiUlM1MTIifQ.eyJpYXQiOjE1NTkxMzIzNzYsImlzcyI6InNtcGwiLCJleHAiOjE1NTkyMjIzNzYsInN1YiI6ImVhcm4iLCJvZmZlciI6eyJhbW91bnQiOjEwLCJpZCI6IjE4MTkwMCJ9LCJyZWNpcGllbnQiOnsiZGVzY3JpcHRpb24iOiJVcGxvYWQgcHJvZmlsZSBwaWN0dXJlIiwiZGV2aWNlX2lkIjoiNTNmNmM2Zjc5NjEzMWY0NiIsInRpdGxlIjoiUmVjZWl2ZWQgS2luIGZyb20gQXZpc2h5IiwidXNlcl9pZCI6InVzZXJfMTQxNzQ5In19.bY_c-mpDzUPNKOajfnAH4To1-9-tl_IekWHVeCSKMByF3dTUbywu4bOe2drlbX4Grd29fb538TlBCaZSMU9ask7LhVIf7YPoiU-PfkAoX4OWByyKTRp_gVRtsee560O26G-7Bh-5l524mGAJgYsEkvMsojVLcSYwZ9HMZTlVurM"
		private const val offerId = "181900"
		private const val orderId = "123"
		private const val transactionId = "some_transaction_id"

	}
}