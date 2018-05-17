package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.Status;
import java.util.concurrent.TimeoutException;

class GetOrderPollingCall extends Thread {

    private static final int[] DELAY_SECONDS = {2, 4, 8, 16, 32, 32, 32, 32, 32};

    private final OrderDataSource.Remote remote;
    private final String orderID;
    private final Callback<Order> callback;

    GetOrderPollingCall(@NonNull final OrderDataSource.Remote remote, final String orderID,
        @NonNull final Callback<Order> callback) {
        this.remote = remote;
        this.orderID = orderID;
        this.callback = callback;
    }

    @Override
    public void run() {
        getOrder(0);
    }

    private void getOrder(int pollingIndex) {
        try {
            if (pollingIndex < DELAY_SECONDS.length) {
                Order order = remote.getOrderSync(orderID);
                System.out.println("GetOrderPollingCall Delay: " + DELAY_SECONDS[pollingIndex]);
                if (order == null || order.getStatus() == Status.PENDING) {
                    sleep(DELAY_SECONDS[pollingIndex] * 1000);
                    getOrder(++pollingIndex);
                } else {
                    callback.onResponse(order);
                }
            } else {
                callback.onFailure(new TimeoutException());
            }
        } catch (final InterruptedException e) {
            callback.onFailure(e);
        }
    }
}