package com.kin.ecosystem.core.data.order

import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.EarnOrderCompletionSubmitted
import com.kin.ecosystem.core.bi.events.EarnOrderCreationReceived
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.blockchain.Payment
import com.kin.ecosystem.core.network.model.*
import com.nhaarman.mockitokotlin2.*
import kin.ecosystem.test.base.BaseTestClass
import kin.sdk.migration.common.KinSdkVersion
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InOrder
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class OutgoingTransferCallTest : BaseTestClass() {

	private val orderDataSource: OrderDataSource = mock {
		on { createExternalOrderSync(any()) } doAnswer { openOrder }
	}
	private val blockchainSource: BlockchainSource = mock()

	private val externalOrderCallbacks: CreateExternalOrderCall.ExternalOrderCallbacks = mock()
	private val outgoingTransferCallback: OutgoingTransferCall.OutgoingTransferCallback = mock()


	private val openOrder: OpenOrder = mock {
		on { offerId } doAnswer { offerId }
		on { id } doAnswer { orderId }
		on { amount } doAnswer { amount }
		on { blockchainData } doAnswer { blockchainData }
		on { title } doAnswer { title }
	}

	private val blockchainData: BlockchainData = mock {
		on { recipientAddress } doAnswer { address }
	}

	private val order: Order = mock {
		on { orderId } doAnswer { orderId }
		on { offerId } doAnswer { offerId }
		on { result } doAnswer { jwtBodyPaymentConfirmationResult }
	}

	private val payload: OutgoingTransfer = mock {
		on { walletAddress} doAnswer { address}
		on { title } doAnswer { title}
		on { description } doAnswer {"description"}
		on { appId } doAnswer {appId}
		on { memo } doAnswer {memo}
		on { amount } doAnswer { amount}
	}

	private val payment: Payment = mock {
		on { orderID } doAnswer { orderId }
		on { isSucceed } doAnswer { true }
		on { transactionID } doAnswer { transactionId }
		on { type } doAnswer { Payment.EARN }
	}

	private val jwtBodyPaymentConfirmationResult: JWTBodyPaymentConfirmationResult = mock {
		on { jwt } doAnswer { "jwt_confirmation" }
	}

	private lateinit var outgoingTransferCall: OutgoingTransferCall

	@Before
	override fun setUp() {
		super.setUp()
		outgoingTransferCall = OutgoingTransferCall(blockchainSource, orderDataSource, payload, title, outgoingTransferCallback)
	}



//	@Test
//	fun `run kin3 happy flow to completion`() {
//		whenever(blockchainSource.blockchainVersion).thenReturn(KinSdkVersion.NEW_KIN_SDK)
//		externalEarnOrderCall.run()
//		val paymentCaptor = argumentCaptor<Observer<Payment>>()
//		inOrder(orderDataSource, blockchainSource, eventLogger, externalOrderCallbacks).apply {
//			verify(orderDataSource).createExternalOrderSync(ExternalEarnOrderCallTest.orderJwt)
//			verify(eventLogger).send(any<EarnOrderCreationReceived>())
//			verify(blockchainSource).addPaymentObservable(paymentCaptor.capture())
//			verify(blockchainSource).blockchainVersion
//			argumentCaptor<KinCallback<Order>>().apply {
//				verify(orderDataSource).submitEarnOrder(any(), isNull(), any(), any(), capture())
//				firstValue.onResponse(order)
//			}
//			verify(eventLogger).send(any<EarnOrderCompletionSubmitted>())
//			paymentCaptor.firstValue.onChanged(payment)
//			verify(blockchainSource).removePaymentObserver(any())
//			argumentCaptor<KinCallback<Order>>().apply {
//				verify(orderDataSource).getOrder(any(), capture())
//				whenever(order.status).doReturn(Order.Status.COMPLETED)
//				firstValue.onResponse(order)
//
//			}
//			verify(externalOrderCallbacks).onOrderConfirmed(jwtBodyPaymentConfirmationResult.jwt, order)
//		}
//	}


	@Test
	fun `OutgoingTransferCallTest`() {
		whenever(blockchainSource.blockchainVersion).thenReturn(KinSdkVersion.NEW_KIN_SDK)
		whenever(orderDataSource.createOutgoingTransferOrderSync(any())).thenReturn(openOrder)
		outgoingTransferCall.run()
		verify(orderDataSource).createOutgoingTransferOrderSync(any())
		val signCaptor = argumentCaptor<BlockchainSource.SignTransactionListener>()
		verify(blockchainSource).signTransaction(any(), any(), any(), any(), signCaptor.capture())
		signCaptor.firstValue.onTransactionSigned(transaction)
		val submitSpendOrderCaptor = argumentCaptor<KinCallback<Order>>()
		verify(orderDataSource).submitSpendOrder(any(), any(), any(), any(), submitSpendOrderCaptor.capture())
		submitSpendOrderCaptor.firstValue.onResponse(order)
	}

	companion object {
		private const val orderJwt = "eyJraWQiOiJyczUxMl8xIiwidHlwIjoiand0IiwiYWxnIjoiUlM1MTIifQ.eyJpYXQiOjE1NTkxMzIzNzYsImlzcyI6InNtcGwiLCJleHAiOjE1NTkyMjIzNzYsInN1YiI6ImVhcm4iLCJvZmZlciI6eyJhbW91bnQiOjEwLCJpZCI6IjE4MTkwMCJ9LCJyZWNpcGllbnQiOnsiZGVzY3JpcHRpb24iOiJVcGxvYWQgcHJvZmlsZSBwaWN0dXJlIiwiZGV2aWNlX2lkIjoiNTNmNmM2Zjc5NjEzMWY0NiIsInRpdGxlIjoiUmVjZWl2ZWQgS2luIGZyb20gQXZpc2h5IiwidXNlcl9pZCI6InVzZXJfMTQxNzQ5In19.bY_c-mpDzUPNKOajfnAH4To1-9-tl_IekWHVeCSKMByF3dTUbywu4bOe2drlbX4Grd29fb538TlBCaZSMU9ask7LhVIf7YPoiU-PfkAoX4OWByyKTRp_gVRtsee560O26G-7Bh-5l524mGAJgYsEkvMsojVLcSYwZ9HMZTlVurM"
		private const val offerId = "181900"
		private const val orderId = "123"
		private const val title = "some title"
		private const val amount = 10
		private const val address = "123456789"
		private const val appId = "1"
		private const val memo = "memo"
		private const val transaction = "transaction"

		private const val transactionId = "some_transaction_id"

	}
}