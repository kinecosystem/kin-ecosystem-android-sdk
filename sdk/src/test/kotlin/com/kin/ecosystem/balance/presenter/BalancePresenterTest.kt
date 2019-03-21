package com.kin.ecosystem.balance.presenter


import com.kin.ecosystem.balance.view.IBalanceView
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.network.model.Order
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kin.ecosystem.test.base.BaseTestClass
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.verify
import java.math.BigDecimal

@RunWith(JUnit4::class)
class BalancePresenterTest : BaseTestClass() {

    private val balanceView: IBalanceView = mock()
    private val blockchainSource: BlockchainSource = mock()
    private val orderRepository: OrderDataSource = mock()
    private val order: Order = mock {
        on { orderId } doAnswer { "2" }
        on { offerId } doAnswer { "1" }
    }


    private val balanceObserverCaptor = argumentCaptor<Observer<Balance>>()
    private val sseCaptor = argumentCaptor<Boolean>()
    private val orderObserverCaptor = argumentCaptor<Observer<Order>>()

    private lateinit var balancePresenter: BalancePresenter

    @Before
    override fun setUp() {
        super.setUp()
        balancePresenter = BalancePresenter(blockchainSource, orderRepository)
        balancePresenter.onAttach(balanceView)

        verify(blockchainSource).addBalanceObserver(balanceObserverCaptor.capture(), sseCaptor.capture())
        verify(orderRepository).addOrderObserver(orderObserverCaptor.capture())
        assertTrue(sseCaptor.firstValue)
    }

    @After
    fun tearDown() {
        balancePresenter.onDetach()

        verify(blockchainSource).removeBalanceObserver(balanceObserverCaptor.firstValue, true)
        verify(orderRepository).removeOrderObserver(orderObserverCaptor.firstValue)
        assertNull(balancePresenter.view)
    }

    @Test
    fun `update balance, check view being notify with the correct value`() {
        val balance = Balance()
        balanceObserverCaptor.firstValue.onChanged(balance)
        verify(balanceView).updateBalance(0)

        balance.amount = BigDecimal(30)
        balanceObserverCaptor.firstValue.onChanged(balance)
        verify(balanceView).updateBalance(30)
    }

    @Test
    fun `pending order start balance loading animation, stop on balance updated`() {
        whenever(order.status).thenReturn(Order.Status.PENDING)
        orderObserverCaptor.firstValue.onChanged(order)
        verify(balanceView).startLoadingAnimation()

        val balance = Balance(BigDecimal(30))
        balanceObserverCaptor.firstValue.onChanged(balance)

        whenever(order.status).thenReturn(Order.Status.COMPLETED)
        orderObserverCaptor.firstValue.onChanged(order)

        verify(balanceView).stopLoadingAnimation()
    }

    @Test
    fun `pending order start balance loading animation, stop on order failed`() {
        whenever(order.status).thenReturn(Order.Status.PENDING)
        orderObserverCaptor.firstValue.onChanged(order)
        verify(balanceView).startLoadingAnimation()


        whenever(order.status).thenReturn(Order.Status.FAILED)
        orderObserverCaptor.firstValue.onChanged(order)
        verify(balanceView).stopLoadingAnimation()
    }

    @Test
    fun `two pending orders start balance loading animation, stop on second update`() {
        whenever(order.status).thenReturn(Order.Status.PENDING)
        orderObserverCaptor.firstValue.onChanged(order)
        orderObserverCaptor.firstValue.onChanged(order)
        verify(balanceView).startLoadingAnimation()

        val balance = Balance(BigDecimal(30))

        // First balance update - should NOT stop animation
        balanceObserverCaptor.firstValue.onChanged(balance)
        whenever(order.status).thenReturn(Order.Status.COMPLETED)
        orderObserverCaptor.firstValue.onChanged(order)
        verify(balanceView, never()).stopLoadingAnimation()

        // Second balance update - should stop animation
        balanceObserverCaptor.firstValue.onChanged(balance)
        whenever(order.status).thenReturn(Order.Status.COMPLETED)
        orderObserverCaptor.firstValue.onChanged(order)

        verify(balanceView).stopLoadingAnimation()
    }
}