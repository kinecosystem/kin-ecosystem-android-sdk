package com.kin.ecosystem.history.model;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.IBaseModel;
import com.kin.ecosystem.network.model.OrderList;

public interface IOrderHistoryModel extends IBaseModel {

    void getHistory(Callback<OrderList> callback);
}
