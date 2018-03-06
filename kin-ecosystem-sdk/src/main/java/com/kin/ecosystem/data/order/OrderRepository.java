package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.StatusEnum;
import com.kin.ecosystem.network.model.OrderList;

public class OrderRepository implements OrderDataSource {

    private static OrderRepository instance = null;

    private final OrderDataSource remoteData;

    private OrderList cachedOrderList;
    private ObservableData<OpenOrder> cachedOpenOrder = ObservableData.create();
    private ObservableData<Order> completedOrder = ObservableData.create();

    private OrderRepository(@NonNull final OrderDataSource remoteData) {
        this.remoteData = remoteData;
    }

    public static void init(@NonNull final OrderDataSource remoteData) {
        if (instance == null) {
            synchronized (OrderRepository.class) {
                instance = new OrderRepository(remoteData);
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
    public void submitOrder(@NonNull String content, @NonNull final String orderID,
        @Nullable final Callback<Order> callback) {
        remoteData.submitOrder(content, orderID, new Callback<Order>() {
            @Override
            public void onResponse(Order response) {
                updateOpenOrder(response);
                setCompletedOrder(response);
                if (callback != null) {
                    callback.onResponse(response);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (callback != null) {
                    callback.onFailure(new TaskFailedException("Order: " + orderID + " submission failed"));
                }
            }
        });
    }

    private void setCompletedOrder(Order order) {
        completedOrder.postValue(order);
    }

    private void updateOpenOrder(Order order) {
        if(cachedOpenOrder.getValue().getId().equals(order.getOrderId())) {
            cachedOpenOrder.postValue(null);
        }
    }

    @Override
    public void cancelOrder(@NonNull final String orderID, @Nullable final Callback<Void> callback) {
        remoteData.cancelOrder(orderID, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                cachedOpenOrder.setValue(null);
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
}
