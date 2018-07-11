package com.kin.ecosystem.marketplace.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseRecyclerAdapter;
import com.chad.library.adapter.base.BaseRecyclerAdapter.OnItemClickListener;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.bi.EventLoggerImpl;
import com.kin.ecosystem.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.exception.ClientException;
import com.kin.ecosystem.history.view.OrderHistoryActivity;
import com.kin.ecosystem.marketplace.presenter.IMarketplacePresenter;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Offer.OfferType;
import com.kin.ecosystem.poll.view.PollWebViewActivity;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.util.List;


public class MarketplaceActivity extends BaseToolbarActivity implements IMarketplaceView {

	private IMarketplacePresenter marketplacePresenter;

    private TextView spendSubTitle;
    private TextView earnSubTitle;
    private SpendRecyclerAdapter spendRecyclerAdapter;
    private EarnRecyclerAdapter earnRecyclerAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.kinecosystem_activity_marketplace;
    }

    @Override
    protected int getTitleRes() {
        return R.string.kinecosystem_kin_marketplace;
    }

    @Override
    protected int getNavigationIcon() {
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
        attachPresenter(new MarketplacePresenter(OfferRepository.getInstance(), OrderRepository.getInstance(),
            BlockchainSourceImpl.getInstance(), EventLoggerImpl.getInstance()));
    }

    @Override
    public void attachPresenter(MarketplacePresenter presenter) {
        marketplacePresenter = presenter;
        marketplacePresenter.onAttach(this);
    }

    @Override
    protected void initViews() {
        spendSubTitle = findViewById(R.id.spend_subtitle);
        earnSubTitle = findViewById(R.id.earn_subtitle);

        //Space item decoration for both of the recyclers
        int margin = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_main_margin);
        int space = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_offer_item_list_space);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(margin, space);

		//Spend Recycler
		RecyclerView spendRecycler = findViewById(R.id.spend_recycler);
		spendRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		spendRecycler.addItemDecoration(itemDecoration);
		spendRecyclerAdapter = new SpendRecyclerAdapter(this);
		spendRecyclerAdapter.bindToRecyclerView(spendRecycler);
		spendRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
				marketplacePresenter.onItemClicked(position, OfferType.SPEND);
			}
		});
		spendRecyclerAdapter.setEmptyView(new OffersEmptyView(this));

		//Earn Recycler
		RecyclerView earnRecycler = findViewById(R.id.earn_recycler);
		earnRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		earnRecycler.addItemDecoration(itemDecoration);
		earnRecyclerAdapter = new EarnRecyclerAdapter(this);
		earnRecyclerAdapter.bindToRecyclerView(earnRecycler);
		earnRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
				marketplacePresenter.onItemClicked(position, OfferType.EARN);
			}
		});
		earnRecyclerAdapter.setEmptyView(new OffersEmptyView(this));

        findViewById(R.id.balance_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marketplacePresenter.balanceItemClicked();
            }
        });
    }

    @Override
    public void onBackPressed() {
        marketplacePresenter.backButtonPressed();
    }

    @Override
    public void navigateBack() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.kinecosystem_slide_out_right);
    }

    @Override
    public void setSpendList(List<Offer> spendList) {
        spendRecyclerAdapter.setNewData(spendList);
    }

    @Override
    public void setEarnList(List<Offer> earnList) {
        earnRecyclerAdapter.setNewData(earnList);
    }

    @Override
    public void navigateToOrderHistory() {
        Intent orderHistory = new Intent(this, OrderHistoryActivity.class);
        navigateToActivity(orderHistory);
    }

    @Override
    public void showOfferActivity(PollBundle pollBundle) {
        try {
            navigateToActivity(PollWebViewActivity.createIntent(this, pollBundle));
        } catch (ClientException e) {
            marketplacePresenter.showOfferActivityFailed();
        }
    }

    @Override
    public void showSpendDialog(ISpendDialogPresenter spendDialogPresenter) {
        SpendDialog spendDialog = new SpendDialog(this, spendDialogPresenter);
        spendDialog.show();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyEarnItemRemoved(int index) {
        earnRecyclerAdapter.notifyItemRemoved(index);
    }

    @Override
    public void notifyEarnItemInserted(int index) {
        earnRecyclerAdapter.notifyItemInserted(index);
    }

    @Override
    public void notifySpendItemRemoved(int index) {
        spendRecyclerAdapter.notifyItemRemoved(index);
    }

    @Override
    public void notifySpendItemInserted(int index) {
        spendRecyclerAdapter.notifyItemInserted(index);
    }

    @Override
    public void showSomethingWentWrong() {
        showToast(getString(R.string.kinecosystem_something_went_wrong));
    }

	@Override
	public void updateEarnSubtitle(boolean isEmpty) {
		earnSubTitle.setText(isEmpty ? R.string.kinecosystem_empty_tomorrow_more_opportunities
			: R.string.kinecosystem_complete_tasks_and_earn_kin);
	}

	@Override
	public void updateSpendSubtitle(boolean isEmpty) {
		spendSubTitle.setText(isEmpty ? R.string.kinecosystem_empty_tomorrow_more_opportunities
			: R.string.kinecosystem_use_your_kin_to_enjoy_stuff_you_like);
	}

	private OffersEmptyView createEmptyView(OffersEmptyView emptyView) {
        if (emptyView == null) {
            emptyView = new OffersEmptyView(this);
        }
        return emptyView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        marketplacePresenter.onDetach();
    }
}
