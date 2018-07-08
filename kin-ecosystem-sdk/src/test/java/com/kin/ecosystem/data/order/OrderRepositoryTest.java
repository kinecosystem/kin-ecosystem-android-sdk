package com.kin.ecosystem.data.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kin.ecosystem.BaseTestClass;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.EarnOrderPaymentConfirmed;
import com.kin.ecosystem.bi.events.SpendOrderCompleted;
import com.kin.ecosystem.bi.events.SpendOrderFailed;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.model.OrderConfirmation;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.exception.DataNotAvailableException;

import com.kin.ecosystem.network.model.Offer.OfferType;
import java.math.BigDecimal;
import kin.ecosystem.core.network.ApiException;

import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.network.model.BlockchainData;
import com.kin.ecosystem.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.Status;
import com.kin.ecosystem.network.model.OrderList;
import com.kin.ecosystem.network.model.OrderSpendResult.TypeEnum;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import kin.ecosystem.core.network.model.Error;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OrderRepositoryTest extends BaseTestClass {

    private static String offerID = "1";
    private static String orderID = "2";

    @Mock
    private BlockchainSource blockchainSource;

    @Mock
    private EventLogger eventLogger;

    @Mock
    private OrderDataSource.Remote remote;

    @Mock
    private OrderDataSource.Local local;

    @Mock
    private KinCallback<OpenOrder> openOrderCallback;

    @Mock
    private OpenOrder openOrder;

    @Mock
    private Offer offer;

    @Mock
    private Order order;

    @Mock
    private Payment payment;

    @Mock
    private BlockchainData blockchainData;

    @Captor
    private ArgumentCaptor<Callback<OpenOrder, ApiException>> createOrderCapture;

    private OrderRepository orderRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        resetInstance();

        when(offer.getId()).thenReturn(offerID);

        when(openOrder.getId()).thenReturn(orderID);
        when(openOrder.getOfferId()).thenReturn(offerID);
        when(openOrder.getAmount()).thenReturn(11);
        when(openOrder.getBlockchainData()).thenReturn(blockchainData);

        when(order.getOrderId()).thenReturn(orderID);
        when(order.getOfferId()).thenReturn(offerID);

        when(blockchainData.getRecipientAddress()).thenReturn("recipientAddress");
        when(blockchainData.getSenderAddress()).thenReturn("senderAddress");
        when(blockchainData.getTransactionId()).thenReturn("someTransactionHash");

        when(payment.getOrderID()).thenReturn(orderID);
        when(payment.isSucceed()).thenReturn(true);
        when(payment.getTransactionID()).thenReturn("transactionID");
    }

    private void resetInstance() throws Exception {
        Field instance = OrderRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        OrderRepository.init(blockchainSource, eventLogger, remote, local);
        orderRepository = OrderRepository.getInstance();
    }

    @Test
    public void getAllCachedOrderHistory_IsNull() {
        assertNull(orderRepository.getAllCachedOrderHistory());
    }

    @Test
    public void getAllOrderHistory_Succeed() {
        KinCallback<OrderList> orderHistoryCallback =  mock(KinCallback.class);
        ArgumentCaptor<Callback<OrderList, ApiException>> orderHistoryCaptor = ArgumentCaptor.forClass(Callback.class);
        orderRepository.getAllOrderHistory(orderHistoryCallback);
        verify(remote).getAllOrderHistory(orderHistoryCaptor.capture());

        orderHistoryCaptor.getValue().onResponse(new OrderList().addOrder(order));

        OrderList orderList = orderRepository.getAllCachedOrderHistory();
        assertNotNull(orderList);
        verify(orderHistoryCallback).onResponse(orderList);
    }

    @Test
    public void createOrder_Succeed() {
        orderRepository.createOrder(offerID, openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());

        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());
        verify(openOrderCallback).onResponse(openOrder);
        verify(openOrderCallback, never()).onFailure(any(KinEcosystemException.class));
    }

    @Test
    public void createOrder_Failed() {
        orderRepository.createOrder(offerID, openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());

        createOrderCapture.getValue().onFailure(getApiException());

        assertNull(orderRepository.getOpenOrder().getValue());
        verify(openOrderCallback).onFailure(any(KinEcosystemException.class));
        verify(openOrderCallback, never()).onResponse(any(OpenOrder.class));
    }


    @Test
    public void submitOrder_Succeed_EarnOrder() throws Exception {
        KinCallback<Order> orderCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<Order, ApiException>> submitOrderCapture = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);
        ArgumentCaptor<Callback<Order, ApiException>> getOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        // Submit Order
        orderRepository.submitOrder(order.getOfferId(), "", order.getOrderId(), orderCallback);
        verify(remote).submitOrder(anyString(), anyString(), submitOrderCapture.capture());
        verify(blockchainSource).addPaymentObservable(paymentCapture.capture());

		when(order.getStatus()).thenReturn(Status.PENDING);
		submitOrderCapture.getValue().onResponse(order);
		verify(orderCallback).onResponse(order);
		assertEquals(order, orderRepository.getOrderWatcher().getValue());

        when(payment.getAmount()).thenReturn(new BigDecimal(20));
        when(payment.isEarn()).thenReturn(true);
        paymentCapture.getValue().onChanged(payment);

        verify(eventLogger).send(any(EarnOrderPaymentConfirmed.class));
        verify(remote).getOrder(anyString(), getOrderCapture.capture());

		when(order.getStatus()).thenReturn(Status.COMPLETED);
        getOrderCapture.getValue().onResponse(order);
        assertNull(orderRepository.getOpenOrder().getValue());
		assertEquals(order, orderRepository.getOrderWatcher().getValue());

        verify(eventLogger, never()).send(any(SpendOrderCompleted.class));
        verify(eventLogger, never()).send(any(SpendOrderFailed.class));
    }

    @Test
    public void submitOrder_Succeed_SpendOrder_StautsCompleted() throws Exception {
        KinCallback<Order> orderCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<Order, ApiException>> submitOrderCapture = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);
        ArgumentCaptor<Callback<Order, ApiException>> getOrderCapture = ArgumentCaptor.forClass(Callback.class);

        when(order.getOfferType()).thenReturn(OfferType.SPEND);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        // Submit Order
        orderRepository.submitOrder(order.getOfferId(), "", order.getOrderId(), orderCallback);
        verify(remote).submitOrder(anyString(), anyString(), submitOrderCapture.capture());
        verify(blockchainSource).addPaymentObservable(paymentCapture.capture());

		when(order.getStatus()).thenReturn(Status.PENDING);
		submitOrderCapture.getValue().onResponse(order);
		verify(orderCallback).onResponse(order);
		assertEquals(order, orderRepository.getOrderWatcher().getValue());

        when(payment.getAmount()).thenReturn(new BigDecimal(-20));
        when(payment.isEarn()).thenReturn(false);
        paymentCapture.getValue().onChanged(payment);

        verify(eventLogger, never()).send(any(EarnOrderPaymentConfirmed.class));
        verify(remote).getOrder(anyString(), getOrderCapture.capture());

		when(order.getStatus()).thenReturn(Status.COMPLETED);
		getOrderCapture.getValue().onResponse(order);
		assertNull(orderRepository.getOpenOrder().getValue());
		assertEquals(order, orderRepository.getOrderWatcher().getValue());

        verify(eventLogger).send(any(SpendOrderCompleted.class));
    }

	@Test
	public void submitOrder_Succeed_SpendOrder_StatusFailed() throws Exception {
		KinCallback<Order> orderCallback = mock(KinCallback.class);
		ArgumentCaptor<Callback<Order, ApiException>> submitOrderCapture = ArgumentCaptor.forClass(Callback.class);
		ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);
		ArgumentCaptor<Callback<Order, ApiException>> getOrderCapture = ArgumentCaptor.forClass(Callback.class);

		when(order.getOfferType()).thenReturn(OfferType.SPEND);

		// Create Order
		orderRepository.createOrder(order.getOfferId(), openOrderCallback);
		verify(remote).createOrder(anyString(), createOrderCapture.capture());
		createOrderCapture.getValue().onResponse(openOrder);
		assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

		// Submit Order
		orderRepository.submitOrder(order.getOfferId(), "", order.getOrderId(), orderCallback);
		verify(remote).submitOrder(anyString(), anyString(), submitOrderCapture.capture());
		verify(blockchainSource).addPaymentObservable(paymentCapture.capture());

		when(order.getStatus()).thenReturn(Status.PENDING);
		submitOrderCapture.getValue().onResponse(order);
		verify(orderCallback).onResponse(order);
		assertEquals(order, orderRepository.getOrderWatcher().getValue());

		when(payment.getAmount()).thenReturn(new BigDecimal(-20));
		when(payment.isEarn()).thenReturn(false);
		paymentCapture.getValue().onChanged(payment);

		verify(eventLogger, never()).send(any(EarnOrderPaymentConfirmed.class));
		verify(remote).getOrder(anyString(), getOrderCapture.capture());

		when(order.getStatus()).thenReturn(Status.FAILED);
		getOrderCapture.getValue().onResponse(order);
		assertNull(orderRepository.getOpenOrder().getValue());
		assertEquals(order, orderRepository.getOrderWatcher().getValue());
	}

    @Test
    public void submitOrder_Failed() throws Exception {
        KinCallback<Order> orderCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<Order, ApiException>> submitOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        // Submit Order
        orderRepository.submitOrder(order.getOfferId(), "", order.getOrderId(), orderCallback);
        verify(remote).submitOrder(anyString(), anyString(), submitOrderCapture.capture());

        submitOrderCapture.getValue().onFailure(getApiException());
        verify(orderCallback).onFailure(any(KinEcosystemException.class));
        verify(orderCallback, never()).onResponse(any(Order.class));
		assertEquals(Status.FAILED, orderRepository.getOrderWatcher().getValue().getStatus());
        assertNull(orderRepository.getOpenOrder().getValue());
    }

    @Test
    public void cancelOrder_Succeed() throws Exception {
        KinCallback<Void> cancelOrderCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<Void, ApiException>> cancelOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        // Cancel Order
        orderRepository.cancelOrder(offerID, orderID, cancelOrderCallback);
        verify(remote).cancelOrder(anyString(), cancelOrderCapture.capture());

        cancelOrderCapture.getValue().onResponse(null);
        assertNull(orderRepository.getOpenOrder().getValue());
    }

    @Test
    public void cancelOrder_Failed() throws Exception {
        KinCallback<Void> cancelOrderCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<Void, ApiException>> cancelOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        // Cancel Order
        orderRepository.cancelOrder(offerID, orderID, cancelOrderCallback);
        verify(remote).cancelOrder(anyString(), cancelOrderCapture.capture());

        cancelOrderCapture.getValue().onFailure(getApiException());
        verify(cancelOrderCallback).onFailure(any(KinEcosystemException.class));
        verify(cancelOrderCallback, never()).onResponse(null);
    }

    @Test
    public void purchase_Spend_Succeed() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);
        ArgumentCaptor<Callback<Order, ApiException>> getOrderCapture = ArgumentCaptor.forClass(Callback.class);

        Order confirmedOrder = new Order().orderId(orderID).offerId(offerID).status(Status.COMPLETED);
        confirmedOrder.setResult(new JWTBodyPaymentConfirmationResult().jwt("A JWT CONFIRMATION").type(TypeEnum.PAYMENT_CONFIRMATION));


        when(remote.createExternalOrderSync(anyString())).thenReturn(openOrder);
        when(remote.getOrderSync(anyString())).thenReturn(confirmedOrder);
        when(openOrder.getOfferType()).thenReturn(OfferType.SPEND);

        orderRepository.purchase("A GENERATED NATIVE OFFER JWT", new KinCallback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation orderConfirmation) {
                countDownLatch.countDown();
                assertEquals("A JWT CONFIRMATION", orderConfirmation.getJwtConfirmation());
                assertEquals(OrderConfirmation.Status.COMPLETED, orderConfirmation.getStatus());
                assertNull(orderRepository.getOpenOrder().getValue());
            }

            @Override
            public void onFailure(KinEcosystemException exception) {

            }
        });
        Thread.sleep(500);
        ShadowLooper.runUiThreadTasks();
        verify(blockchainSource, times(2)).addPaymentObservable(paymentCapture.capture());
        List<Observer<Payment>> observersList = paymentCapture.getAllValues();
        for (Observer<Payment> observer : observersList) {
            observer.onChanged(payment);
        }

        verify(remote).getOrder(anyString(), getOrderCapture.capture());
        List<Callback<Order, ApiException>> getOrderCallbackList = getOrderCapture.getAllValues();
        for (Callback<Order, ApiException> callback : getOrderCallbackList) {
            callback.onResponse(confirmedOrder);
        }
        assertEquals(Status.COMPLETED, orderRepository.getOrderWatcher().getValue().getStatus());

        countDownLatch.await(1000, TimeUnit.MICROSECONDS);
        ShadowLooper.runUiThreadTasks();
    }

    @Test
    public void purchase_Conflict_GetOrderConfirmation() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Order confirmedOrder = new Order().orderId(orderID).offerId(offerID).status(Status.COMPLETED);
        confirmedOrder.setResult(
            new JWTBodyPaymentConfirmationResult().jwt("A JWT CONFIRMATION").type(TypeEnum.PAYMENT_CONFIRMATION));

        // Create the Conflict error response
        Error error = Mockito.mock(Error.class);
        when(error.getCode()).thenReturn(4091);

        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put("location", Arrays.asList("v/some/OrderID12"));

        ApiException apiException = Mockito.mock(ApiException.class);
        when(apiException.getCode()).thenReturn(409);
        when(apiException.getResponseBody()).thenReturn(error);
        when(apiException.getResponseHeaders()).thenReturn(responseHeaders);

        when(remote.createExternalOrderSync(anyString())).thenThrow(apiException);
        when(remote.getOrderSync(anyString())).thenReturn(confirmedOrder);

        // Check not error, and got jwt confirmation
        orderRepository.purchase("A GENERATED NATIVE OFFER JWT", new KinCallback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation orderConfirmation) {
                countDownLatch.countDown();
                assertEquals("A JWT CONFIRMATION", orderConfirmation.getJwtConfirmation());
				assertEquals(OrderConfirmation.Status.COMPLETED, orderConfirmation.getStatus());
                assertNull(orderRepository.getOpenOrder().getValue());
            }

            @Override
            public void onFailure(KinEcosystemException exception) {

            }
        });

        Thread.sleep(500);
        ShadowLooper.runUiThreadTasks();
        verify(blockchainSource, never()).addPaymentObservable(any(Observer.class));
        verify(remote, never()).submitOrder(anyString(), anyString(), any(Callback.class));
        assertNull(orderRepository.getOrderWatcher().getValue());

        countDownLatch.await(1000, TimeUnit.MICROSECONDS);
        ShadowLooper.runUiThreadTasks();

    }

    @Test
    public void purchase_Failed_Cant_Create_Order() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        when(remote.createExternalOrderSync(anyString())).thenThrow(getApiException());

        orderRepository.purchase("generatedOfferJWT", new KinCallback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation confirmationJwt) {

            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                countDownLatch.countDown();
                assertNotNull(exception);
                assertNull(orderRepository.getOpenOrder().getValue());
            }
        });
        Thread.sleep(500);
        ShadowLooper.runUiThreadTasks();
        verify(blockchainSource, never()).addPaymentObservable(any(Observer.class));
		verify(remote, never()).submitOrder(anyString(), anyString(), any(Callback.class));
		assertNull(orderRepository.getOrderWatcher().getValue());

        countDownLatch.await(500, TimeUnit.MICROSECONDS);
    }

    @Test
    public void purchase_Spend_Failed_Payment_Failed() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);

        when(remote.createExternalOrderSync(anyString())).thenReturn(openOrder);
        when(payment.isSucceed()).thenReturn(false);
        when(openOrder.getOfferType()).thenReturn(OfferType.SPEND);

        orderRepository.purchase("generatedOfferJWT", new KinCallback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation response) {

            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                countDownLatch.countDown();
                assertNotNull(exception);
                assertNull(orderRepository.getOpenOrder().getValue());
            }
        });
        Thread.sleep(500);
        ShadowLooper.runUiThreadTasks();
        verify(blockchainSource, times(2)).addPaymentObservable(paymentCapture.capture());
        List<Observer<Payment>> observersList = paymentCapture.getAllValues();
        for (Observer<Payment> observer : observersList) {
            observer.onChanged(payment);
        }

        countDownLatch.await(500, TimeUnit.MICROSECONDS);

        verify(blockchainSource).removePaymentObserver(observersList.get(0));
        verify(blockchainSource).removePaymentObserver(observersList.get(1));

        assertNull(orderRepository.getOrderWatcher().getValue());
    }

    @Test
    public void isFirstSpendOrder_True() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Callback<Boolean,Void>> isFirstSpendCallback = ArgumentCaptor.forClass(Callback.class);
        orderRepository.isFirstSpendOrder(new KinCallback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                countDownLatch.countDown();
                assertEquals(true, response);
            }

            @Override
            public void onFailure(KinEcosystemException exception) {

            }
        });
        verify(local).isFirstSpendOrder(isFirstSpendCallback.capture());
        isFirstSpendCallback.getValue().onResponse(true);
        countDownLatch.await(500, TimeUnit.MICROSECONDS);
    }

    @Test
    public void isFirstSpendOrder_False() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Callback<Boolean, Void>> isFirstSpendCallback = ArgumentCaptor.forClass(Callback.class);
        orderRepository.isFirstSpendOrder(new KinCallback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                countDownLatch.countDown();
                assertTrue(exception.getCause() instanceof DataNotAvailableException);
            }
        });
        verify(local).isFirstSpendOrder(isFirstSpendCallback.capture());
        isFirstSpendCallback.getValue().onFailure(null);
        countDownLatch.await(500, TimeUnit.MICROSECONDS);
    }

    @Test
    public void setIsFirstSpendOrder_true() throws Exception {
        orderRepository.setIsFirstSpendOrder(false);
        verify(local).setIsFirstSpendOrder(false);

        orderRepository.setIsFirstSpendOrder(true);
        verify(local).setIsFirstSpendOrder(true);
    }

    private ApiException getApiException() {
        Exception exception = new IllegalArgumentException();
        ApiException apiException = new ApiException(500,exception);
        return apiException;
    }
}