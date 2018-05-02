package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.data.order.CreateExternalOrderCall.ExternalOrderCallbacks;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.OrderList;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderRepository implements OrderDataSource {

    private static final String TAG = OrderRepository.class.getSimpleName();

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
                callback.onFailure(new DataNotAvailableException());
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
                    callback.onFailure(new TaskFailedException("Could not create order for offer: " + offerID));
                }
            }
        });
    }

    @Override
    public void submitOrder(@NonNull final String offerID, @Nullable String content, @NonNull final String orderID,
        @Nullable final Callback<Order> callback) {
        listenForCompletedPayment();
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
                    callback.onFailure(new TaskFailedException("Order: " + orderID + " submission failed"));
                }
            }
        });
    }

    private void listenForCompletedPayment() {
        if (pendingOrdersCount.getAndIncrement() == 0) {
            paymentObserver = new Observer<Payment>() {
                @Override
                public void onChanged(Payment payment) {
                    getOrder(payment.getOrderID());
                    decrementPendingOrdersCount();
                }
            };
            blockchainSource.addPaymentObservable(paymentObserver);
            Log.d(TAG, "listenForCompletedPayment: addPaymentObservable");
        }
    }

    private void getOrder(final String orderID) {
        remoteData.getOrder(orderID, new Callback<Order>() {
            @Override
            public void onResponse(Order order) {
                setCompletedOrder(order);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void decrementPendingOrdersCount() {
        if (hasMorePendingOffers()) {
            pendingOrdersCount.decrementAndGet();
        } else {
            if (paymentObserver != null) {
                blockchainSource.removePaymentObserver(paymentObserver);
                Log.d(TAG, "decrementPendingOrdersCount: removePaymentObserver");
            }
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
        decrementPendingOrdersCount();
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
                    callback.onFailure(new TaskFailedException("Could not cancel order: " + orderID));
                }
            }
        });
    }

    @Override
    public void purchase(String offerJwt, @Nullable final Callback<String> callback) {
        new CreateExternalOrderCall(remoteData, blockchainSource, offerJwt, new ExternalOrderCallbacks() {
            @Override
            public void onTransactionSent(OpenOrder openOrder) {
                cachedOpenOrder.postValue(openOrder);
                submitOrder(openOrder.getOfferId(), null, openOrder.getId(), null);
            }

            @Override
            public void onTransactionFailed(OpenOrder openOrder, String msg) {
                cancelOrder(openOrder.getOfferId(), openOrder.getId(), null);
                if (callback != null) {
                    callback.onFailure(new TaskFailedException(msg));
                }
            }

            @Override
            public void onOrderConfirmed(String confirmationJwt) {
                if (callback != null) {
                    callback.onResponse(confirmationJwt);
                }
            }

            @Override
            public void onOrderFailed(String msg) {
                if (callback != null) {
                    callback.onFailure(new TaskFailedException(msg));
                }
            }
        }).start();
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
}
