package com.kin.ecosystem.data.order;

import static com.kin.ecosystem.exception.ClientException.INTERNAL_INCONSISTENCY;

import android.support.annotation.NonNull;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.Status;
import com.kin.ecosystem.util.ErrorUtil;

class GetOrderPollingCall extends Thread {

    private static final int[] DELAY_SECONDS = {2, 4, 8, 16, 32, 32, 32, 32, 32};
    private static final int SEC_IN_MILLI = 1000;

    private final OrderDataSource.Remote remote;
    private final String orderID;
    private final Callback<Order, ApiException> callback;

    GetOrderPollingCall(@NonNull final OrderDataSource.Remote remote, final String orderID,
        @NonNull final Callback<Order, ApiException> callback) {
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
                if (order == null || order.getStatus() == Status.PENDING) {
                    sleep(DELAY_SECONDS[pollingIndex] * SEC_IN_MILLI);
                    getOrder(++pollingIndex);
                } else {
                    callback.onResponse(order);
                }
            } else {
                callback.onFailure(ErrorUtil.getTimeoutException());
            }
        } catch (final InterruptedException e) {
            callback.onFailure(toApiException(e));
        }
    }

    private ApiException toApiException(InterruptedException e) {
        return new ApiException(INTERNAL_INCONSISTENCY, e);
    }
}