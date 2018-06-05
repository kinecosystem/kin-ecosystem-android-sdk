package com.kin.ecosystem.data.order;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.model.OrderConfirmation;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.BlockchainData;
import com.kin.ecosystem.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.Status;
import com.kin.ecosystem.network.model.OrderList;
import com.kin.ecosystem.network.model.OrderSpendResult.TypeEnum;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OrderRepositoryTest {

    private static String offerID = "1";
    private static String orderID = "2";

    @Mock
    private BlockchainSource blockchainSource;

    @Mock
    private OfferDataSource offerRepository;

    @Mock
    private OrderDataSource.Remote remote;

    @Mock
    private OrderDataSource.Local local;

    @Mock
    private Callback<OpenOrder> openOrderCallback;

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
    private ArgumentCaptor<Callback<OpenOrder>> createOrderCapture;

    private OrderRepository orderRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        resetInstance();
        resetPendingOrdersCount();
        resetPaymentObserverCount();

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
    }

    private void resetPendingOrdersCount() throws Exception {
        Field pendingOrdersCount = OrderRepository.class.getDeclaredField("pendingOrdersCount");
        pendingOrdersCount.setAccessible(true);
        pendingOrdersCount.set(pendingOrdersCount, new AtomicInteger(0));
    }

    private void resetPaymentObserverCount() throws Exception {
        Field paymentObserverCount = OrderRepository.class.getDeclaredField("paymentObserverCount");
        paymentObserverCount.setAccessible(true);
        paymentObserverCount.set(paymentObserverCount, new AtomicInteger(0));
    }

    private void resetInstance() throws Exception {
        Field instance = OrderRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        OrderRepository.init(blockchainSource, offerRepository, remote, local);
        orderRepository = OrderRepository.getInstance();
    }

    @Test
    public void getAllCachedOrderHistory_IsNull() {
        assertNull(orderRepository.getAllCachedOrderHistory());
    }

    @Test
    public void getAllOrderHistory_Succeed() {
        Callback<OrderList> orderHistoryCallback = (Callback<OrderList>) mock(Callback.class);
        ArgumentCaptor<Callback<OrderList>> orderHistoryCaptor = ArgumentCaptor.forClass(Callback.class);
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
        verify(openOrderCallback, never()).onFailure(any(Throwable.class));
    }

    @Test
    public void createOrder_Failed() {
        orderRepository.createOrder(offerID, openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());

        TaskFailedException exception = new TaskFailedException("Some error");
        createOrderCapture.getValue().onFailure(exception);
        assertNull(orderRepository.getOpenOrder().getValue());
        verify(openOrderCallback).onFailure(exception);
        verify(openOrderCallback, never()).onResponse(any(OpenOrder.class));
    }


    @Test
    public void submitOrder_Succeed() throws Exception {
        Callback<Order> orderCallback = (Callback<Order>) mock(Callback.class);
        ArgumentCaptor<Callback<Order>> submitOrderCapture = ArgumentCaptor.forClass(Callback.class);
        ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);
        ArgumentCaptor<Callback<Order>> getOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        // Submit Order
        orderRepository.submitOrder(order.getOfferId(), "", order.getOrderId(), orderCallback);
        verify(remote).submitOrder(anyString(), anyString(), submitOrderCapture.capture());
        verify(offerRepository, times(1)).setPendingOfferByID(order.getOfferId());
        verify(blockchainSource).addPaymentObservable(paymentCapture.capture());

        paymentCapture.getValue().onChanged(payment);
        verify(remote).getOrder(anyString(), getOrderCapture.capture());

        orderRepository.addCompletedOrderObserver(new Observer<Order>() {
            @Override
            public void onChanged(Order value) {
                assertEquals(order, value);
            }
        });

        ObservableData<Offer> pendingOffer = ObservableData.create(offer);
        when(offerRepository.getPendingOffer()).thenReturn(pendingOffer);
        getOrderCapture.getValue().onResponse(order);
        assertNull(orderRepository.getOpenOrder().getValue());
        verify(offerRepository).setPendingOfferByID(null);

        submitOrderCapture.getValue().onResponse(order);
        verify(orderCallback).onResponse(order);
    }

    @Test
    public void submitOrder_Failed() throws Exception {
        Callback<Order> orderCallback = (Callback<Order>) mock(Callback.class);
        ArgumentCaptor<Callback<Order>> submitOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        // Submit Order
        orderRepository.submitOrder(order.getOfferId(), "", order.getOrderId(), orderCallback);
        verify(remote).submitOrder(anyString(), anyString(), submitOrderCapture.capture());
        verify(offerRepository, times(1)).setPendingOfferByID(order.getOfferId());

        ObservableData<Offer> pendingOffer = ObservableData.create(offer);
        when(offerRepository.getPendingOffer()).thenReturn(pendingOffer);

        submitOrderCapture.getValue().onFailure(new Throwable());
        verify(orderCallback).onFailure(any(Throwable.class));
        verify(orderCallback, never()).onResponse(any(Order.class));
        assertNull(orderRepository.getOpenOrder().getValue());
        verify(offerRepository).setPendingOfferByID(null);
    }

    @Test
    public void cancelOrder_Succeed() throws Exception {
        Callback<Void> cancelOrderCallback = (Callback<Void>) mock(Callback.class);
        ArgumentCaptor<Callback<Void>> cancelOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        ObservableData<Offer> pendingOffer = ObservableData.create(offer);
        when(offerRepository.getPendingOffer()).thenReturn(pendingOffer);

        // Cancel Order
        orderRepository.cancelOrder(offerID, orderID, cancelOrderCallback);
        verify(remote).cancelOrder(anyString(), cancelOrderCapture.capture());

        cancelOrderCapture.getValue().onResponse(null);
        assertNull(orderRepository.getOpenOrder().getValue());
        verify(offerRepository).setPendingOfferByID(null);
    }

    @Test
    public void cancelOrder_Failed() throws Exception {
        Callback<Void> cancelOrderCallback = (Callback<Void>) mock(Callback.class);
        ArgumentCaptor<Callback<Void>> cancelOrderCapture = ArgumentCaptor.forClass(Callback.class);

        // Create Order
        orderRepository.createOrder(order.getOfferId(), openOrderCallback);
        verify(remote).createOrder(anyString(), createOrderCapture.capture());
        createOrderCapture.getValue().onResponse(openOrder);
        assertEquals(openOrder, orderRepository.getOpenOrder().getValue());

        ObservableData<Offer> pendingOffer = ObservableData.create(offer);
        when(offerRepository.getPendingOffer()).thenReturn(pendingOffer);

        // Cancel Order
        orderRepository.cancelOrder(offerID, orderID, cancelOrderCallback);
        verify(remote).cancelOrder(anyString(), cancelOrderCapture.capture());

        cancelOrderCapture.getValue().onFailure(any(Throwable.class));
        verify(cancelOrderCallback).onFailure(any(Throwable.class));
        verify(cancelOrderCallback, never()).onResponse(null);
    }

    @Test
    public void purchase_Succeed() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);
        ArgumentCaptor<Callback<Order>> getOrderCapture = ArgumentCaptor.forClass(Callback.class);

        Order confirmedOrder = new Order().orderId(orderID).offerId(offerID).status(Status.COMPLETED);
        confirmedOrder.setResult(new JWTBodyPaymentConfirmationResult().jwt("A JWT CONFIRMATION").type(TypeEnum.PAYMENT_CONFIRMATION));
        ObservableData<Offer> pendingOffer = ObservableData.create(offer);

        when(remote.createExternalOrderSync(anyString())).thenReturn(openOrder);
        when(remote.getOrderSync(anyString())).thenReturn(confirmedOrder);
        when(offerRepository.getPendingOffer()).thenReturn(pendingOffer);

        orderRepository.purchase("A GENERATED NATIVE OFFER JWT", new Callback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation orderConfirmation) {
                countDownLatch.countDown();
                assertEquals("A JWT CONFIRMATION", orderConfirmation.getJwtConfirmation());
                verify(offerRepository).setPendingOfferByID(null);
                assertNull(orderRepository.getOpenOrder().getValue());
            }

            @Override
            public void onFailure(Throwable t) {

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
        List<Callback<Order>> getOrderCallbackList = getOrderCapture.getAllValues();
        for (Callback<Order> callback : getOrderCallbackList) {
            callback.onResponse(confirmedOrder);
        }
        countDownLatch.await(1000, TimeUnit.MICROSECONDS);
        ShadowLooper.runUiThreadTasks();

        verify(offerRepository).setPendingOfferByID(offerID);
    }

    @Test
    public void purchase_Failed_Cant_Create_Order() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        when(remote.createExternalOrderSync(anyString())).thenThrow(new ApiException());

        orderRepository.purchase("generatedOfferJWT", new Callback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation confirmationJwt) {

            }

            @Override
            public void onFailure(Throwable t) {
                countDownLatch.countDown();
                assertNotNull(t);
                assertNull(orderRepository.getOpenOrder().getValue());
            }
        });
        Thread.sleep(500);
        ShadowLooper.runUiThreadTasks();
        verify(blockchainSource, never()).addPaymentObservable(any(Observer.class));

        countDownLatch.await(500, TimeUnit.MICROSECONDS);
    }

    @Test
    public void purchase_Failed_Payment_Failed() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Observer<Payment>> paymentCapture = ArgumentCaptor.forClass(Observer.class);
        ArgumentCaptor<Callback<Void>> cancelOrderCallback = ArgumentCaptor.forClass(Callback.class);

        ObservableData<Offer> pendingOffer = ObservableData.create(offer);
        when(offerRepository.getPendingOffer()).thenReturn(pendingOffer);
        when(remote.createExternalOrderSync(anyString())).thenReturn(openOrder);
        when(payment.isSucceed()).thenReturn(false);

        orderRepository.purchase("generatedOfferJWT", new Callback<OrderConfirmation>() {
            @Override
            public void onResponse(OrderConfirmation response) {

            }

            @Override
            public void onFailure(Throwable t) {
                countDownLatch.countDown();
                assertNotNull(t);
                verify(offerRepository).setPendingOfferByID(null);
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

        verify(remote).cancelOrder(anyString(), cancelOrderCallback.capture());
        cancelOrderCallback.getValue().onResponse(null);

        countDownLatch.await(500, TimeUnit.MICROSECONDS);

        verify(blockchainSource).removePaymentObserver(observersList.get(0));
        verify(blockchainSource).removePaymentObserver(observersList.get(1));
    }

    @Test
    public void isFirstSpendOrder_True() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Callback<Boolean>> isFirstSpendCallback = ArgumentCaptor.forClass(Callback.class);
        orderRepository.isFirstSpendOrder(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                countDownLatch.countDown();
                assertEquals(true, response);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        verify(local).isFirstSpendOrder(isFirstSpendCallback.capture());
        isFirstSpendCallback.getValue().onResponse(true);
        countDownLatch.await(500, TimeUnit.MICROSECONDS);
    }

    @Test
    public void isFirstSpendOrder_False() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ArgumentCaptor<Callback<Boolean>> isFirstSpendCallback = ArgumentCaptor.forClass(Callback.class);
        orderRepository.isFirstSpendOrder(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {

            }

            @Override
            public void onFailure(Throwable t) {
                countDownLatch.countDown();
                assertTrue(t instanceof DataNotAvailableException);
            }
        });
        verify(local).isFirstSpendOrder(isFirstSpendCallback.capture());
        isFirstSpendCallback.getValue().onFailure(new Throwable());
        countDownLatch.await(500, TimeUnit.MICROSECONDS);
    }

    @Test
    public void setIsFirstSpendOrder_true() throws Exception {
        orderRepository.setIsFirstSpendOrder(false);
        verify(local).setIsFirstSpendOrder(false);

        orderRepository.setIsFirstSpendOrder(true);
        verify(local).setIsFirstSpendOrder(true);
    }
}