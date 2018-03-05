package com.kin.ecosystem.history.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
        return R.layout.activity_order_history;
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
        attachPresenter(new OrderHistoryPresenter());
    }

    @Override
    protected void initViews() {
        RecyclerView orderRecyclerView = findViewById(R.id.order_history_recycler);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderHistoryRecyclerAdapter = new OrderHistoryRecyclerAdapter();
        orderHistoryRecyclerAdapter.bindToRecyclerView(orderRecyclerView);
    }

    @Override
    public void attachPresenter(OrderHistoryPresenter presenter) {
        transactionHistoryPresenter = presenter;
        transactionHistoryPresenter.onAttach(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marketplace, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.info_menu == id) {
            //TODO handle info clicked
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateOrderHistoryList(List<Order> orders) {
        orderHistoryRecyclerAdapter.setNewData(orders);
        orderHistoryRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transactionHistoryPresenter.onDetach();
    }
}
