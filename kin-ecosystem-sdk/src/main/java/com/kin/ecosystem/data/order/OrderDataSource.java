package com.kin.ecosystem.data.order;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.model.OrderList;

interface OrderDataSource {

    OrderList getAllCachedOrderHistory();

    void getAllOrderHistory(Callback<OrderList> callback);
}
