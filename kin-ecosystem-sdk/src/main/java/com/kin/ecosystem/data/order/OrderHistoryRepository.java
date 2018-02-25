package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.network.model.OrderList;

public class OrderHistoryRepository implements OrderDataSource {

    private static OrderHistoryRepository instance = null;

    private final OrderHistoryRemoteData remoteData;

    private OrderList cachedOrderList;

    private OrderHistoryRepository(@NonNull OrderHistoryRemoteData remoteData) {
        this.remoteData = remoteData;
    }

    public static void init(@NonNull OrderHistoryRemoteData remoteData) {
        if (instance == null) {
            synchronized (OrderHistoryRepository.class) {
                instance = new OrderHistoryRepository(remoteData);
            }
        }
    }

    public static OrderHistoryRepository getInstance() {
        return instance;
    }

    @Override
    public OrderList getAllCachedOrderHistory() {
        return cachedOrderList;
    }

    @Override
    public void getAllOrderHistory(final Callback<OrderList> callback) {
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
}
