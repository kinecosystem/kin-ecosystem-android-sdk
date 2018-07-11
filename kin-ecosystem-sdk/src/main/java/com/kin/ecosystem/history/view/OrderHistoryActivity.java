package com.kin.ecosystem.history.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.chad.library.adapter.base.BaseRecyclerAdapter;
import com.chad.library.adapter.base.BaseRecyclerAdapter.OnItemClickListener;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.base.IBottomDialogPresenter;
import com.kin.ecosystem.bi.EventLoggerImpl;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.history.presenter.ICouponDialogPresenter;
import com.kin.ecosystem.history.presenter.IOrderHistoryPresenter;
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter;
import com.kin.ecosystem.network.model.Order;
import java.util.List;


public class OrderHistoryActivity extends BaseToolbarActivity implements IOrderHistoryView {

    private IOrderHistoryPresenter orderHistoryPresenter;
    private OrderHistoryRecyclerAdapter orderHistoryRecyclerAdapter;

    private static final String IS_FIRST_SPEND_ORDER = "is_first_spend_order";

    public static Intent createIntent(@NonNull Context context, boolean isFirstSpendOrder) {
        final Intent intent = new Intent(context, OrderHistoryActivity.class);
        intent.putExtra(IS_FIRST_SPEND_ORDER, isFirstSpendOrder);
        return intent;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.kinecosystem_activity_order_history;
    }

    @Override
    protected int getTitleRes() {
        return R.string.kinecosystem_transaction_history;
    }

    @Override
    public int getNavigationIcon() {
        return R.drawable.kinecosystem_ic_back;
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
        boolean isFirstSpendOrder = getIntent().getBooleanExtra(IS_FIRST_SPEND_ORDER, false);
        attachPresenter(new OrderHistoryPresenter(OrderRepository.getInstance(), EventLoggerImpl.getInstance(), isFirstSpendOrder));
    }

    @Override
    protected void initViews() {
        RecyclerView orderRecyclerView = findViewById(R.id.order_history_recycler);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderHistoryRecyclerAdapter = new OrderHistoryRecyclerAdapter();
        orderHistoryRecyclerAdapter.bindToRecyclerView(orderRecyclerView);
        orderHistoryRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
                orderHistoryPresenter.onItemCLicked(position);
            }
        });
    }

    @Override
    public void attachPresenter(OrderHistoryPresenter presenter) {
        orderHistoryPresenter = presenter;
        orderHistoryPresenter.onAttach(this);
    }

    @Override
    public void updateOrderHistoryList(List<Order> orders) {
        orderHistoryRecyclerAdapter.setNewData(orders);
        orderHistoryRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemInserted() {
        orderHistoryRecyclerAdapter.notifyItemInserted(0);
    }

    @Override
    public void onItemUpdated(int index) {
        orderHistoryRecyclerAdapter.notifyItemChanged(index);
    }

    @Override
    public void showCouponDialog(@NonNull ICouponDialogPresenter presenter) {
        CouponDialog couponDialog = new CouponDialog(this, presenter);
        couponDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderHistoryPresenter.onDetach();
    }
}
