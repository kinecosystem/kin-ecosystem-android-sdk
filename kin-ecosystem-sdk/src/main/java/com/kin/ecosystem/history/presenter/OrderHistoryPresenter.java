package com.kin.ecosystem.history.presenter;


import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.history.view.IOrderHistoryView;
import com.kin.ecosystem.network.model.OrderList;

public class OrderHistoryPresenter extends BasePresenter<IOrderHistoryView> {

    private final OrderRepository repository;

    private OrderList orderHistoryList;

    public OrderHistoryPresenter() {
        this.repository = OrderRepository.getInstance();
    }

    @Override
    public void onAttach(IOrderHistoryView view) {
        super.onAttach(view);
        getOrderHistoryList();
    }

    private void getOrderHistoryList() {
        orderHistoryList = repository.getAllCachedOrderHistory();
        setOrderHistoryList(orderHistoryList);
        repository.getAllOrderHistory(new Callback<OrderList>() {
            @Override
            public void onResponse(OrderList orderHistoryList) {
                syncNewOrders(orderHistoryList);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void syncNewOrders(OrderList newOrdersList) {
        if (orderHistoryList != null && orderHistoryList.getOrders() != null
            && orderHistoryList.getOrders().size() > 0) {
            newOrdersList.getOrders().removeAll(orderHistoryList.getOrders());
        }
        setOrderHistoryList(newOrdersList);
    }

    private void setOrderHistoryList(OrderList orderHistoryList) {
        if (orderHistoryList != null && orderHistoryList.getOrders() != null) {
            this.orderHistoryList = orderHistoryList;
            this.view.addToOrderHistoryList(orderHistoryList.getOrders());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        release();
    }

    private void release() {
        orderHistoryList = null;
    }
}
