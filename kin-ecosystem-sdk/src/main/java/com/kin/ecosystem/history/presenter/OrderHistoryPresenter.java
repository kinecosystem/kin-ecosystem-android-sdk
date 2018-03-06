package com.kin.ecosystem.history.presenter;


import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.history.view.IOrderHistoryView;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.OrderList;
import java.util.List;

public class OrderHistoryPresenter extends BasePresenter<IOrderHistoryView> {

    private static final int NOT_FOUND = -1;
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
            List<Order> oldList = orderHistoryList.getOrders();
            List<Order> newList = newOrdersList.getOrders();
            //the oldest order is the last one, so we'll go from the last and add the top
            //we will end with newest order at the top.
            for (int i = newList.size() - 1; i >= 0; i--) {
                Order order = newList.get(i);
                int index = oldList.indexOf(order);
                if (index == NOT_FOUND) {
                    //add at top
                    oldList.add(order);
                } else {
                    //Update
                    oldList.set(index, order);
                }
            }
            orderHistoryList.setOrders(oldList);
        } else {
            orderHistoryList = newOrdersList;
        }
        setOrderHistoryList(orderHistoryList);
    }

    private void setOrderHistoryList(OrderList orderHistoryList) {
        if (orderHistoryList != null && orderHistoryList.getOrders() != null) {
            this.orderHistoryList = orderHistoryList;
            this.view.updateOrderHistoryList(orderHistoryList.getOrders());
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
