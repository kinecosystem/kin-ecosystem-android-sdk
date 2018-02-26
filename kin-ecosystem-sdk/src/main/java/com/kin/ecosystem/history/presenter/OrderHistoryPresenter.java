package com.kin.ecosystem.history.presenter;


import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.data.order.OrderHistoryRepository;
import com.kin.ecosystem.history.view.IOrderHistoryView;
import com.kin.ecosystem.network.model.OrderList;

public class OrderHistoryPresenter implements IBasePresenter {

    private final OrderHistoryRepository repository;
    private IOrderHistoryView orderHistoryView;

    private OrderList orderHistoryList;

    public OrderHistoryPresenter(IOrderHistoryView view) {
        this.orderHistoryView = view;
        this.repository = OrderHistoryRepository.getInstance();
    }

    @Override
    public void onAttach() {
        getOrderHistoryList();
    }

    private void getOrderHistoryList() {
        orderHistoryList = repository.getAllCachedOrderHistory();
        setOrderHistoryList(orderHistoryList);
        repository.getAllOrderHistory(new Callback<OrderList>() {
            @Override
            public void onResponse(OrderList orderHistoryList) {
                setOrderHistoryList(orderHistoryList);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void setOrderHistoryList(OrderList orderHistoryList) {
        if (orderHistoryList != null && orderHistoryList.getOrders() != null) {
            this.orderHistoryList = orderHistoryList;
            this.orderHistoryView.addToOrderHistoryList(orderHistoryList.getOrders());
        }
    }

    @Override
    public void onDetach() {
        release();
    }

    private void release() {
        orderHistoryView = null;
        orderHistoryList = null;
    }
}
