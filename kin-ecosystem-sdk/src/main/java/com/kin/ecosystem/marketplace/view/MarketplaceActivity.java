package com.kin.ecosystem.marketplace.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kin.ecosystem.R;
import com.kin.ecosystem.marketplace.viewmodel.IMarketplaceViewModel;
import com.kin.ecosystem.marketplace.viewmodel.MarketplaceViewModel;
import com.kin.ecosystem.network.model.Offer;

import java.util.List;


public class MarketplaceActivity extends BaseToolbarActivity implements IMarketplaceView {

    private IMarketplaceViewModel marketplaceViewModel;

    private SpendRecyclerAdapter spendRecyclerAdapter;
    private EarnRecyclerAdapter earnRecyclerAdapter;

    @Override
    int getLayoutRes() {
        return R.layout.marketplace_activity;
    }

    @Override
    int getNavigationIcon() {
        return R.drawable.ic_back;
    }

    @Override
    View.OnClickListener getNavigationClickListener() {
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
        attachViewModel();
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

    private void initViews() {
        //Spend Recycler
        RecyclerView spendRecycler = findViewById(R.id.spend_recycler);
        spendRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        spendRecyclerAdapter = new SpendRecyclerAdapter();
        spendRecyclerAdapter.bindToRecyclerView(spendRecycler);

        //Earn Recycler
        RecyclerView earnRecycler = findViewById(R.id.earn_recycler);
        earnRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        earnRecyclerAdapter = new EarnRecyclerAdapter();
        earnRecyclerAdapter.bindToRecyclerView(earnRecycler);
    }

    private void attachViewModel() {
        marketplaceViewModel = new MarketplaceViewModel(this);
        marketplaceViewModel.onAttach();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public void updateSpendList(List<Offer> spendList) {
        spendRecyclerAdapter.setNewData(spendList);
    }

    @Override
    public void updateEarnList(List<Offer> earnList) {
        earnRecyclerAdapter.setNewData(earnList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        marketplaceViewModel.onDetach();
    }
}
