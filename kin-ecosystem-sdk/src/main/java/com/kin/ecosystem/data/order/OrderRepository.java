package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.CallbackAdapter;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.model.OrderConfirmation;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.data.order.CreateExternalOrderCall.ExternalOrderCallbacks;
import com.kin.ecosystem.data.order.CreateExternalOrderCall.ExternalSpendOrderCallbacks;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.OrderList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderRepository implements OrderDataSource {

    private static final String TAG = OrderRepository.class.getSimpleName();
    private static final String ORIGIN_EXTERNAL = "external";

    private static OrderRepository instance = null;
    private final OrderDataSource.Local localData;
    private final OrderDataSource.Remote remoteData;

    private final OfferDataSource offerRepository;
    private final IBlockchainSource blockchainSource;

    private OrderList cachedOrderList;
    private ObservableData<OpenOrder> cachedOpenOrder = ObservableData.create();
    private ObservableData<Order> completedOrder = ObservableData.create();
    private Observer<Payment> paymentObserver;

    private static volatile AtomicInteger pendingOrdersCount = new AtomicInteger(0);
    private static volatile AtomicInteger paymentObserverCount = new AtomicInteger(0);

    private OrderRepository(@NonNull final IBlockchainSource blockchainSource,
        @NonNull final OfferDataSource offerRepository, @NonNull final OrderDataSource.Remote remoteData,
        @NonNull final OrderDataSource.Local localData) {
        this.remoteData = remoteData;
        this.localData = localData;
        this.offerRepository = offerRepository;
        this.blockchainSource = blockchainSource;
    }

    public static void init(@NonNull final IBlockchainSource blockchainSource,
        @NonNull final OfferDataSource offerRepository, @NonNull final OrderDataSource.Remote remoteData,
        @NonNull final OrderDataSource.Local localData) {
        if (instance == null) {
            synchronized (OrderRepository.class) {
                if (instance == null) {
                    instance = new OrderRepository(blockchainSource, offerRepository, remoteData, localData);
                }
            }
        }
    }

    public static OrderRepository getInstance() {
        return instance;
    }

    public OrderList getAllCachedOrderHistory() {
        return cachedOrderList;
    }

    @Override
    public void getAllOrderHistory(@NonNull final Callback<OrderList> callback) {
        remoteData.getAllOrderHistory(new Callback<OrderList>() {
            @Override
            public void onResponse(OrderList response) {
                cachedOrderList = response;
                callback.onResponse(response);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public ObservableData<OpenOrder> getOpenOrder() {
        return cachedOpenOrder;
    }

    @Override
    public void createOrder(@NonNull final String offerID, @Nullable final Callback<OpenOrder> callback) {
        remoteData.createOrder(offerID, new Callback<OpenOrder>() {
            @Override
            public void onResponse(OpenOrder response) {
                cachedOpenOrder.postValue(response);
                if (callback != null) {
                    callback.onResponse(response);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (callback != null) {
                    callback.onFailure(t);
                }
            }
        });
    }

    @Override
    public void submitOrder(@NonNull final String offerID, @Nullable String content, @NonNull final String orderID,
        @Nullable final Callback<Order> callback) {
        listenForCompletedPayment();
        pendingOrdersCount.incrementAndGet();
        offerRepository.setPendingOfferByID(offerID);
        remoteData.submitOrder(content, orderID, new Callback<Order>() {
            @Override
            public void onResponse(Order response) {
                if (callback != null) {
                    callback.onResponse(response);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                removeCachedOpenOrderByID(orderID);
                removePendingOfferByID(offerID);
                if (callback != null) {
                    callback.onFailure(t);
                }
            }
        });
    }

    private void listenForCompletedPayment() {
        if (paymentObserverCount.getAndIncrement() == 0) {
            paymentObserver = new Observer<Payment>() {
                @Override
                public void onChanged(Payment payment) {
                    decrementPaymentObserverCount();
                    getOrder(payment.getOrderID());
                }
            };
            blockchainSource.addPaymentObservable(paymentObserver);
            Log.d(TAG, "listenForCompletedPayment: addPaymentObservable");
        }
    }

    private void decrementPaymentObserverCount() {
        if (paymentObserverCount.get() > 0 &&
            paymentObserverCount.decrementAndGet() == 0 &&
            paymentObserver != null) {

            blockchainSource.removePaymentObserver(paymentObserver);
        }
    }

    private void getOrder(final String orderID) {
        remoteData.getOrder(orderID, new Callback<Order>() {
            @Override
            public void onResponse(Order order) {
                decrementPendingOrdersCount();
                setCompletedOrder(order);
            }

            @Override
            public void onFailure(Throwable t) {
                decrementPendingOrdersCount();
            }
        });
    }

    private void decrementPendingOrdersCount() {
        if (hasMorePendingOffers()) {
            pendingOrdersCount.decrementAndGet();
        }
    }

    private void setCompletedOrder(@NonNull Order order) {
        Log.i(TAG, "setCompletedOrder: " + order);
        completedOrder.postValue(order);
        if (!hasMorePendingOffers()) {
            removeCachedOpenOrderByID(order.getOrderId());
            removePendingOfferByID(order.getOfferId());
        }
    }

    private boolean hasMorePendingOffers() {
        return pendingOrdersCount.get() > 0;
    }

    private void removeCachedOpenOrderByID(String orderId) {
        if (isCachedOpenOrderEquals(orderId)) {
            cachedOpenOrder.postValue(null);
        }
    }

    private void removePendingOfferByID(String offerId) {
        if (isCurrentPendingOfferIdEquals(offerId)) {
            offerRepository.setPendingOfferByID(null);
        }
    }

    private boolean isCachedOpenOrderEquals(String orderId) {
        if (cachedOpenOrder != null) {
            final OpenOrder openOrder = cachedOpenOrder.getValue();
            if (openOrder != null) {
                return openOrder.getId().equals(orderId);
            }
        }
        return false;
    }

    private boolean isCurrentPendingOfferIdEquals(String offerId) {
        if (offerRepository.getPendingOffer() != null) {
            final Offer pendingOffer = offerRepository.getPendingOffer().getValue();
            if (pendingOffer != null) {
                return pendingOffer.getId().equals(offerId);
            }
        }
        return false;
    }

    @Override
    public void cancelOrder(@NonNull final String offerID, @NonNull final String orderID,
        @Nullable final Callback<Void> callback) {
        decrementCount();
        remoteData.cancelOrder(orderID, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                removeCachedOpenOrderByID(orderID);
                removePendingOfferByID(offerID);
                if (callback != null) {
                    callback.onResponse(response);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (callback != null) {
                    callback.onFailure(new TaskFailedException(getApiExceptionsMessage(t)));
                }
            }
        });
    }

    @Override
    public void purchase(String offerJwt, @Nullable final Callback<OrderConfirmation> callback) {
        new ExternalSpendOrderCall(remoteData, blockchainSource, offerJwt, new ExternalSpendOrderCallbacks() {
            @Override
            public void onOrderCreated(OpenOrder openOrder) {
                cachedOpenOrder.postValue(openOrder);
            }

            @Override
            public void onTransactionSent(OpenOrder openOrder) {
                submitOrder(openOrder.getOfferId(), null, openOrder.getId(), null);
            }

            @Override
            public void onTransactionFailed(OpenOrder openOrder, final String msg) {
                cancelOrder(openOrder.getOfferId(), openOrder.getId(), new Callback<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        handleOnFailure(msg);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        handleOnFailure(msg);
                    }
                });

            }

            @Override
            public void onOrderConfirmed(String confirmationJwt) {
                if (callback != null) {
                    callback.onResponse(createOrderConfirmation(confirmationJwt));
                }
            }

            @Override
            public void onOrderFailed(String msg) {
                decrementCount();
                handleOnFailure(msg);
            }

            private void handleOnFailure(String msg) {
                if (callback != null) {
                    callback.onFailure(new TaskFailedException(msg));
                }
            }

        }).start();
    }

    @Override
    public void requestPayment(String offerJwt, final Callback<OrderConfirmation> callback) {
        new ExternalEarnOrderCall(remoteData, blockchainSource, offerJwt, new ExternalOrderCallbacks() {
            @Override
            public void onOrderCreated(OpenOrder openOrder) {
                cachedOpenOrder.postValue(openOrder);
                submitOrder(openOrder.getOfferId(), null, openOrder.getId(), new CallbackAdapter<Order>() {
                    @Override
                    public void onFailure(Throwable t) {
                        handleOnFailure(getApiExceptionsMessage(t));
                    }
                });
            }

            @Override
            public void onOrderConfirmed(String confirmationJwt) {
                if (callback != null) {
                    callback.onResponse(createOrderConfirmation(confirmationJwt));
                }
            }

            @Override
            public void onOrderFailed(String msg) {
                decrementCount();
                handleOnFailure(msg);
            }

            private void handleOnFailure(String msg) {
                if (callback != null) {
                    callback.onFailure(new TaskFailedException(msg));
                }
            }
        }).start();
    }

    private OrderConfirmation createOrderConfirmation(String confirmationJwt) {
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setStatus(OrderConfirmation.Status.COMPLETED);
        orderConfirmation.setJwtConfirmation(confirmationJwt);
        return orderConfirmation;
    }

    @Override
    public void addCompletedOrderObserver(@NonNull Observer<Order> observer) {
        completedOrder.addObserver(observer);
    }

    @Override
    public void removeCompletedOrderObserver(@NonNull Observer<Order> observer) {
        completedOrder.removeObserver(observer);
    }

    @Override
    public void isFirstSpendOrder(@NonNull final Callback<Boolean> callback) {
        localData.isFirstSpendOrder(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                callback.onResponse(response);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(new DataNotAvailableException());
            }
        });
    }

    @Override
    public void setIsFirstSpendOrder(boolean isFirstSpendOrder) {
        localData.setIsFirstSpendOrder(isFirstSpendOrder);
    }

    @Override
    public void getExternalOrderStatus(@NonNull String offerID, @NonNull final Callback<OrderConfirmation> callback) {
        remoteData.getFilteredOrderHistory(ORIGIN_EXTERNAL, offerID, new Callback<OrderList>() {
            @Override
            public void onResponse(OrderList response) {
                if (response != null) {
                    final List<Order> orders = response.getOrders();
                    if (orders != null && orders.size() > 0) {
                        final Order order = orders.get(orders.size());
                        OrderConfirmation orderConfirmation = new OrderConfirmation();
                        OrderConfirmation.Status status = OrderConfirmation.Status
                            .fromValue(order.getStatus().getValue());
                        orderConfirmation.setStatus(status);
                        if (status == OrderConfirmation.Status.COMPLETED) {
                            try {
                                orderConfirmation
                                    .setJwtConfirmation(
                                        ((JWTBodyPaymentConfirmationResult) order.getResult()).getJwt());
                            } catch (ClassCastException e) {
                                Log.d(TAG, "could not cast to jwt confirmation");
                                callback.onFailure(new DataNotAvailableException());
                            }

                        }
                        callback.onResponse(orderConfirmation);
                    } else {
                        callback.onFailure(new DataNotAvailableException());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    private void decrementCount() {
        decrementPendingOrdersCount();
        decrementPaymentObserverCount();
    }

    private String getApiExceptionsMessage(Throwable t) {
        try {
            return ((ApiException) t).getResponseBody().getMessage();
        } catch (Exception e) {
            return "task failed";
        }
    }
}
