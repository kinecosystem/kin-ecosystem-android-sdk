package com.kin.ecosystem.history.presenter;


import com.kin.ecosystem.Callback;
import com.kin.ecosystem.history.model.IOrderHistoryModel;
import com.kin.ecosystem.history.model.OrderHistoryModel;
import com.kin.ecosystem.history.view.IOrderHistoryView;
import com.kin.ecosystem.network.model.OrderList;

public class OrderHistoryPresenter implements IOrderHistoryPresenter {

    private final IOrderHistoryModel transactionHistoryModel = new OrderHistoryModel();
    private IOrderHistoryView transactionHistoryView;

    private OrderList transactionHistoryList;

    public OrderHistoryPresenter(IOrderHistoryView view) {
        this.transactionHistoryView = view;
    }

    @Override
    public void onAttach() {
        this.transactionHistoryModel.getHistory(new Callback<OrderList>() {
            @Override
            public void onResponse(OrderList transactionsList) {
                if(transactionsList != null && transactionsList.getOrders() != null) {
                    transactionHistoryList = transactionsList;
                    transactionHistoryView.addToHistoryList(transactionsList.getOrders());
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
        transactionHistoryModel.release();
        transactionHistoryView = null;
        transactionHistoryList = null;
    }
}
