package com.kin.ecosystem.history.presenter;


import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.IBottomDialogPresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.Coupon;
import com.kin.ecosystem.data.model.Coupon.CouponCode;
import com.kin.ecosystem.data.model.Coupon.CouponInfo;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.history.view.ICouponDialog;
import com.kin.ecosystem.history.view.IOrderHistoryView;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.StatusEnum;
import com.kin.ecosystem.network.model.OrderList;
import java.util.Iterator;
import java.util.List;

public class OrderHistoryPresenter extends BasePresenter<IOrderHistoryView> implements IOrderHistoryPresenter {

    private static final int NOT_FOUND = -1;
    private final OrderDataSource orderRepository;

    private OrderList orderHistoryList;
    private Observer<Order> completedOrderObserver;
    private final Gson gson;

    private boolean isFirstSpendOrder;

    public OrderHistoryPresenter(@NonNull final OrderDataSource orderRepository, boolean isFirstSpendOrder) {
        this.orderRepository = orderRepository;
        this.isFirstSpendOrder = isFirstSpendOrder;
        this.gson = new Gson();
    }

    @Override
    public void onAttach(IOrderHistoryView view) {
        super.onAttach(view);
        getOrderHistoryList();
        listenToCompletedOrders();
    }

    private void getOrderHistoryList() {
        orderHistoryList = orderRepository.getAllCachedOrderHistory();
        setOrderHistoryList(orderHistoryList);
        orderRepository.getAllOrderHistory(new Callback<OrderList>() {
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
                    //add at top (ui orientation)
                    orderHistoryList.addOrderAtIndex(0, order);
                    notifyItemInserted();
                } else {
                    //Update
                    orderHistoryList.updateOrder(index, order);
                    notifyItemUpdated(index);
                }
            }

        } else {
            orderHistoryList = newOrdersList;
            setOrderHistoryList(orderHistoryList);
        }
    }

    private void setOrderHistoryList(OrderList orderHistoryList) {
        if (orderHistoryList != null && orderHistoryList.getOrders() != null) {
            List<Order> orders = orderHistoryList.getOrders();
            removePendingOrders(orders);
            this.orderHistoryList = orderHistoryList;
            this.view.updateOrderHistoryList(orderHistoryList.getOrders());
        }
    }

    private void removePendingOrders(List<Order> orders) {
        for (Iterator<Order> ordersIterator = orders.listIterator(); ordersIterator.hasNext(); ) {
            Order order = ordersIterator.next();
            if (order.getStatus() == StatusEnum.PENDING) {
                ordersIterator.remove();
            }
        }
    }

    private void listenToCompletedOrders() {
        completedOrderObserver = new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                addOrderOrUpdate(order);
                if (isFirstSpendOrder) {
                    showCouponDialog(order);
                }
            }
        };
        orderRepository.addCompletedOrderObserver(completedOrderObserver);
    }

    private void addOrderOrUpdate(Order order) {
        int index = orderHistoryList.contains(order);
        if (index == NOT_FOUND) {
            orderHistoryList.addOrderAtIndex(0, order);
            notifyItemInserted();
        } else {
            orderHistoryList.updateOrder(index, order);
            notifyItemUpdated(index);
        }
    }

    private void notifyItemInserted() {
        if (view != null) {
            view.onItemInserted();
        }
    }

    private void notifyItemUpdated(int index) {
        if (view != null) {
            view.onItemUpdated(index);
        }
    }

    @Override
    public void onItemCLicked(int position) {
        Order order = orderHistoryList.get(position);
        if (order != null) {
            showCouponDialog(order);
        }
    }

    private void showCouponDialog(Order order) {
        Coupon coupon = deserializeCoupon(order);
        if (view != null && coupon != null) {
            view.showCouponDialog(createCouponDialogPresenter(coupon));
        }
    }

    private IBottomDialogPresenter<ICouponDialog> createCouponDialogPresenter(Coupon coupon) {
        return new CouponDialogPresenter(coupon);
    }

    private Coupon deserializeCoupon(Order order) {
        try {
            CouponInfo couponInfo = gson.fromJson(order.getContent(), CouponInfo.class);
            CouponCode couponCode = gson.fromJson(order.getResult().toString(), CouponCode.class);
            return new Coupon(couponInfo, couponCode);
        } catch (Exception t) {
            return null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        release();
    }

    private void release() {
        orderRepository.removeCompletedOrderObserver(completedOrderObserver);
        orderHistoryList = null;
    }
}
