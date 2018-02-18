package com.kin.ecosystem.history.view;

import com.kin.ecosystem.network.model.Order;

import java.util.List;

public interface IOrderHistoryView {

    void addToOrderHistoryList(List<Order> orders);
}
