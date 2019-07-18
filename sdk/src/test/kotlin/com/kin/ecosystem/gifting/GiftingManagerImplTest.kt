package com.kin.ecosystem.gifting

import com.kin.ecosystem.FakeActivity
import com.kin.ecosystem.JwtProvider
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.KinTheme
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.ServiceException
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.common.model.OrderConfirmation
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.APageViewed
import com.kin.ecosystem.core.bi.events.GiftingFlowCompleted
import com.kin.ecosystem.core.bi.events.PageCloseTapped
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.internal.Configuration
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.nhaarman.mockitokotlin2.*
import kin.ecosystem.test.base.BaseTestClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kin.ecosystem.appreciation.options.menu.ui.CloseType
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class GiftingManagerImplTest: BaseTestClass() {

	private val balance: Balance = mock {
		on { amount } doAnswer { BigDecimal.TEN }
	}

	private val jwtProvider: JwtProvider = mock()
	private val blockchainSource: BlockchainSource = mock {
		on { balance } doAnswer { balance }
	}


	private val orderDataSource: OrderDataSource = mock()
	private val eventLogger: EventLogger = mock()
	private val configuration: Configuration = mock {
		on { kinTheme } doAnswer { KinTheme.LIGHT }
	}

	private lateinit var giftingManager: GiftingManagerImpl

	@Before
	override fun setUp() {
		super.setUp()
		giftingManager = GiftingManagerImpl(jwtProvider, blockchainSource, orderDataSource, eventLogger, configuration)
	}

	@Test
	fun `show dialog only once, if is showing already`() {
		val activity = Robolectric.buildActivity(FakeActivity::class.java).get()
		giftingManager.showDialog(activity, recipientUserID)
		verify(blockchainSource).balance
		verify(configuration).kinTheme

		giftingManager.showDialog(activity, recipientUserID)
		verifyNoMoreInteractions(blockchainSource)
		verifyNoMoreInteractions(configuration)
	}

	@Test
	fun `on dialog closed, send the correct event`() {
		val eventCaptor = argumentCaptor<PageCloseTapped>()

		giftingManager.onDialogClosed(CloseType.CLOSE_BUTTON)
		giftingManager.onDialogClosed(CloseType.TOUCH_OUTSIDE)
		giftingManager.onDialogClosed(CloseType.BACK_NAV_BUTTON)
		verify(eventLogger, times(3)).send(eventCaptor.capture())
		assertEquals(PageCloseTapped.ExitType.X_BUTTON, eventCaptor.allValues[0].exitType)
		assertEquals(PageCloseTapped.ExitType.BACKGROUND_APP, eventCaptor.allValues[1].exitType)
		assertEquals(PageCloseTapped.ExitType.ANDROID_NAVIGATOR, eventCaptor.allValues[2].exitType)

		giftingManager.onDialogClosed(CloseType.ITEM_SELECTED)
		verify(eventLogger).send(any<GiftingFlowCompleted>())
	}

	@Test
	fun `on dialog opened send the correct event`() {
		val eventCaptor = argumentCaptor<APageViewed>()

		giftingManager.onDialogOpened()
		verify(eventLogger).send(eventCaptor.capture())
		assertEquals(APageViewed.PageName.GIFTING_DIALOG, eventCaptor.firstValue.pageName)
	}

	@Test
	fun `on item selected, order succeed, handler notify for succeed`() {
		val validJWT = "some_valid_jwt"
		val validJwtConfirmation = "jwtConfirmation"
		val orderConfirmation = OrderConfirmation().apply {
			status = OrderConfirmation.Status.COMPLETED
			jwtConfirmation = validJwtConfirmation
		}
		val activity = Robolectric.buildActivity(FakeActivity::class.java).get()
		val purchaseCaptor = argumentCaptor<KinCallback<OrderConfirmation>>()
		whenever(jwtProvider.getPayToUserJwt(recipientUserID, 10.0)).thenReturn(validJWT)


		val confirmObserver: Observer<OrderConfirmation> = mock()
		giftingManager.addOrderConfirmationObserver(confirmObserver)
		giftingManager.showDialog(activity, recipientUserID)
		giftingManager.onItemSelected(1, 10)
		verify(orderDataSource).purchase(any(), purchaseCaptor.capture())
		purchaseCaptor.firstValue.onResponse(orderConfirmation)
		verify(confirmObserver).onChanged(orderConfirmation)
	}

	@Test
	fun `on item selected, order failed, handler notify for failure`() {
		val validJWT = "some_valid_jwt"
		val serviceException = ServiceException(ServiceException.NETWORK_ERROR, "some error msg", null)
		val activity = Robolectric.buildActivity(FakeActivity::class.java).get()
		val purchaseCaptor = argumentCaptor<KinCallback<OrderConfirmation>>()
		whenever(jwtProvider.getPayToUserJwt(recipientUserID, 10.0)).thenReturn(validJWT)


		val confirmObserver: Observer<OrderConfirmation> = mock()
		giftingManager.addOrderConfirmationObserver(confirmObserver)
		giftingManager.showDialog(activity, recipientUserID)
		giftingManager.onItemSelected(1, 10)
		verify(orderDataSource).purchase(any(), purchaseCaptor.capture())
		purchaseCaptor.firstValue.onFailure(serviceException)

		val orderCaptor = argumentCaptor<OrderConfirmation>()
		verify(confirmObserver).onChanged(orderCaptor.capture())
		orderCaptor.firstValue.apply {
			assertEquals(serviceException, exception)
			assertEquals(OrderConfirmation.Status.FAILED, status)
		}
	}

	companion object {
		private const val recipientUserID = "recipientUserID"
	}
}