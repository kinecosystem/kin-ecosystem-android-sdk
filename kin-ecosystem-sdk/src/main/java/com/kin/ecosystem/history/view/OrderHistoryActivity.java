package com.kin.ecosystem.history.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter;
import com.kin.ecosystem.network.model.Order;

import java.util.List;


public class OrderHistoryActivity extends BaseToolbarActivity implements IOrderHistoryView {

    private OrderHistoryPresenter transactionHistoryPresenter;
    private OrderHistoryRecyclerAdapter orderHistoryRecyclerAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.transaction_history_activity;
    }

    @Override
    protected int getTitleRes() {
        return R.string.transaction_history;
    }

    @Override
    public int getNavigationIcon() {
        return R.drawable.ic_back;
    }

    @Override
    protected View.OnClickListener getNavigationClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        attachPresenter();
    }

    @Override
    protected void initViews() {
        RecyclerView orderRecyclerView = findViewById(R.id.order_history_recycler);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderHistoryRecyclerAdapter = new OrderHistoryRecyclerAdapter();
        orderHistoryRecyclerAdapter.bindToRecyclerView(orderRecyclerView);

    }

    private void attachPresenter() {
        transactionHistoryPresenter = new OrderHistoryPresenter(this);
        transactionHistoryPresenter.onAttach();
    }

    @Override
    public void addToHistoryList(List<Order> transactions) {
        int lastIndex = orderHistoryRecyclerAdapter.getDataCount();
        orderHistoryRecyclerAdapter.addData(lastIndex, transactions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transactionHistoryPresenter.onDetach();

    }
}
