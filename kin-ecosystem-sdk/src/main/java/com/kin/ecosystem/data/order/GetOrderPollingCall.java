package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.api.OrdersApi;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.StatusEnum;
import java.util.concurrent.TimeoutException;

class GetOrderPollingCall extends Thread {

    private static final int DELAY = 2000;

    private final OrdersApi ordersApi;
    private final String orderID;
    private final int pollingMaxValue;
    private final Callback<Order> callback;

    GetOrderPollingCall(@NonNull final OrdersApi ordersApi, final String orderID, final int pollingMaxValue,
        @NonNull final Callback<Order> callback) {
        this.ordersApi = ordersApi;
        this.orderID = orderID;
        this.pollingMaxValue = pollingMaxValue;
        this.callback = callback;
    }

    @Override
    public void run() {
        getOrder(0);
    }

    private void getOrder(int pollingValue) {
        try {
            if (pollingValue < pollingMaxValue) {
                Order order = ordersApi.getOrder(orderID, "");
                if (order.getStatus() == StatusEnum.PENDING) {
                    sleep(DELAY);
                    getOrder(++pollingValue);
                } else {
                    callback.onResponse(order);
                }
            }
            else {
                callback.onFailure(new TimeoutException());
            }
        } catch (final ApiException | InterruptedException e) {
            callback.onFailure(e);
        }
    }
}