package com.kin.ecosystem.marketplace.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kin.ecosystem.R;

import com.kin.ecosystem.marketplace.viewmodel.IMarketplaceViewModel;
import com.kin.ecosystem.marketplace.viewmodel.MarketplaceViewModel;
import com.kin.ecosystem.network.model.Offer;

import java.util.List;


public class MarketplaceActivity extends AppCompatActivity implements IMarketplaceView {

    private IMarketplaceViewModel marketplaceViewModel;

    private SpendRecyclerAdapter spendRecyclerAdapter;
    private EarnRecyclerAdapter earnRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_activity);
        initViews();
        attachViewModel();
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
