package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.Order.Status;

class GetOrderPollingCall extends Thread {

    private static final int[] DELAY_SECONDS = {2, 4, 8, 16, 32, 32, 32, 32, 32};
    private static final int SEC_IN_MILLI = 1000;
    private static final int DELAYED_ATTEMPTED_NUMBER = 5;

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
                    if(order != null && pollingIndex == DELAYED_ATTEMPTED_NUMBER){
                        callback.onResponse(order.status(Status.DELAYED));
                    }
                    sleep(DELAY_SECONDS[pollingIndex] * SEC_IN_MILLI);
                    getOrder(++pollingIndex);
                } else {
                    callback.onResponse(order);
                }
            } else {
                callback.onFailure(ErrorUtil.createOrderTimeoutException());
            }
        } catch (final InterruptedException e) {
            callback.onFailure(toApiException(e));
        }
    }

    private ApiException toApiException(InterruptedException e) {
        return new ApiException(ClientException.INTERNAL_INCONSISTENCY, e);
    }
}