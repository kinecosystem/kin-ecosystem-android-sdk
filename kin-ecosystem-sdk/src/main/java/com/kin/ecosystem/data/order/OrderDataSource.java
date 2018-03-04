package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.OrderList;

interface OrderDataSource {

    OrderList getAllCachedOrderHistory();

    void getAllOrderHistory(@NonNull final Callback<OrderList> callback);

    ObservableData<OpenOrder> getOpenOrder();

    void createOrder(@NonNull final String offerID, final Callback<OpenOrder> callback);

    void submitOrder(@NonNull String content, @NonNull String orderID, final Callback<Order> callback);

    void cancelOrder(@NonNull final String orderID, final Callback<Void> callback);
}
