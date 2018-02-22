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
        orderHistoryList = repository.getAllCachedOrderHistory();
        if (orderHistoryList != null) {
            orderHistoryView.addToOrderHistoryList(orderHistoryList.getOrders());
        }
        repository.getAllOrderHistory(new Callback<OrderList>() {
            @Override
            public void onResponse(OrderList transactionsList) {
                if (transactionsList != null && transactionsList.getOrders() != null) {
                    orderHistoryList = transactionsList;
                    orderHistoryView.addToOrderHistoryList(orderHistoryList.getOrders());
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

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
