package com.kin.ecosystem.marketplace.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.history.view.OrderHistoryActivity;
import com.kin.ecosystem.marketplace.presenter.MarketplaceViewPresenter;
import com.kin.ecosystem.network.model.Offer;

import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.poll.view.PollWebViewActivity;
import java.util.List;


public class MarketplaceActivity extends BaseToolbarActivity implements IMarketplaceView {

    private MarketplaceViewPresenter marketplacePresenter;

    private SpendRecyclerAdapter spendRecyclerAdapter;
    private EarnRecyclerAdapter earnRecyclerAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_marketplace;
    }

    @Override
    protected int getTitleRes() {
        return R.string.kin_marketplace;
    }

    @Override
    protected int getNavigationIcon() {
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
        attachPresenter(new MarketplaceViewPresenter(OfferRepository.getInstance(), OrderRepository.getInstance()));
    }

    @Override
    public void attachPresenter(MarketplaceViewPresenter presenter) {
        marketplacePresenter = presenter;
        marketplacePresenter.onAttach(this);
        spendRecyclerAdapter.setOnItemClickListener(marketplacePresenter);
        earnRecyclerAdapter.setOnItemClickListener(marketplacePresenter);
    }

    @Override
    protected void initViews() {
        //Space item decoration for both of the recyclers
        int margin = getResources().getDimensionPixelOffset(R.dimen.main_margin);
        int space = getResources().getDimensionPixelOffset(R.dimen.offer_item_list_space);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(margin, space);

        //Spend Recycler
        RecyclerView spendRecycler = findViewById(R.id.spend_recycler);
        spendRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        spendRecycler.addItemDecoration(itemDecoration);
        spendRecyclerAdapter = new SpendRecyclerAdapter();
        spendRecyclerAdapter.bindToRecyclerView(spendRecycler);

        //Earn Recycler
        RecyclerView earnRecycler = findViewById(R.id.earn_recycler);
        earnRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        earnRecycler.addItemDecoration(itemDecoration);
        earnRecyclerAdapter = new EarnRecyclerAdapter();
        earnRecyclerAdapter.bindToRecyclerView(earnRecycler);

        findViewById(R.id.balance_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToTransactionHistory();
            }
        });
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
    public void moveToTransactionHistory() {
        Intent transactionHistory = new Intent(this, OrderHistoryActivity.class);
        navigateToActivity(transactionHistory);
    }

    @Override
    public void showOfferActivity(Offer offer) {
        navigateToActivity(PollWebViewActivity.createIntent(this, offer.getContent(), offer.getId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        marketplacePresenter.onDetach();
    }
}
