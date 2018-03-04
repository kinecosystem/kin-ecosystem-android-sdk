package com.kin.ecosystem.history.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter;
import com.kin.ecosystem.network.model.Order;

import java.util.List;

public interface IOrderHistoryView extends IBaseView<OrderHistoryPresenter>{

    void addToOrderHistoryList(List<Order> orders);
}
