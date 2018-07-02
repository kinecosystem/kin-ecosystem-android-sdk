package com.kin.ecosystem.main.view;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.View;
import com.kin.ecosystem.R;
import com.kin.ecosystem.balance.presenter.BalancePresenter;
import com.kin.ecosystem.balance.presenter.IBalancePresenter;
import com.kin.ecosystem.balance.presenter.IBalancePresenter.BalanceClickListener;
import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.bi.EventLoggerImpl;
import com.kin.ecosystem.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.history.presenter.IOrderHistoryPresenter;
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter;
import com.kin.ecosystem.history.view.OrderHistoryFragment;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.main.presenter.EcosystemPresenter;
import com.kin.ecosystem.main.presenter.IEcosystemPresenter;
import com.kin.ecosystem.marketplace.presenter.IMarketplacePresenter;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.marketplace.view.MarketplaceFragment;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class EcosystemActivity extends BaseToolbarActivity implements IEcosystemView, INavigator {

	public static final int MARKETPLACE = 0x00000001;
	public static final int ORDER_HISTORY = 0x00000002;

	@IntDef({MARKETPLACE, ORDER_HISTORY})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ScreenId {

	}

	public static final String ECOSYSTEM_MARKETPLACE_FRAGMENT_TAG = "ecosystem_marketplace_fragment_tag";
	public static final String ECOSYSTEM_ORDER_HISTORY_FRAGMENT_TAG = "ecosystem_order_history_fragment_tag";

	private IBalancePresenter balancePresenter;
	private IEcosystemPresenter ecosystemPresenter;
	private IMarketplacePresenter marketplacePresenter;
	private IOrderHistoryPresenter orderHistoryPresenter;

	@Override
	protected int getLayoutRes() {
		return R.layout.kinecosystem_activity_main;
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
		IBalanceView balanceView = findViewById(R.id.balance_view);
		balancePresenter = new BalancePresenter(balanceView, BlockchainSourceImpl.getInstance(),
			OfferRepository.getInstance(), OrderRepository.getInstance());
		balancePresenter.onAttach(balanceView);
		balancePresenter.setClickListener(new BalanceClickListener() {
			@Override
			public void onClick() {
				ecosystemPresenter.balanceItemClicked();
			}
		});
		ecosystemPresenter = new EcosystemPresenter(this, this);
	}

	@Override
	public void updateTitle(String title) {
		getToolbar().setTitle(title);
	}

	@Override
	public void navigateToMarketplace() {
		MarketplaceFragment marketplaceFragment = (MarketplaceFragment) getSupportFragmentManager()
			.findFragmentByTag(ECOSYSTEM_MARKETPLACE_FRAGMENT_TAG);
		if (marketplaceFragment == null) {
			marketplaceFragment = MarketplaceFragment.newInstance();
		}

		marketplacePresenter = new MarketplacePresenter(marketplaceFragment, OfferRepository.getInstance(),
			OrderRepository.getInstance(),
			BlockchainSourceImpl.getInstance(),
			this,
			EventLoggerImpl.getInstance());

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, marketplaceFragment, ECOSYSTEM_MARKETPLACE_FRAGMENT_TAG)
			.commit();

		setVisibleScreen(MARKETPLACE);
	}

	private void setVisibleScreen(@ScreenId final int id) {
		ecosystemPresenter.visibleScreen(id);
		balancePresenter.visibleScreen(id);
	}

	@Override
	public void navigateToOrderHistory(boolean isFirstSpendOrder) {
		OrderHistoryFragment orderHistoryFragment = (OrderHistoryFragment) getSupportFragmentManager()
			.findFragmentByTag(ECOSYSTEM_ORDER_HISTORY_FRAGMENT_TAG);

		if (orderHistoryFragment == null) {
			orderHistoryFragment = OrderHistoryFragment.newInstance();
		}

		orderHistoryPresenter = new OrderHistoryPresenter(orderHistoryFragment,
			OrderRepository.getInstance(),
			this,
			EventLoggerImpl.getInstance(),
			isFirstSpendOrder);

		getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(
				R.anim.kinecosystem_slide_in_right,
				R.anim.kinecosystem_slide_out_left,
				R.anim.kinecosystem_slide_in_left,
				R.anim.kinecosystem_slide_out_right)
			.replace(R.id.fragment_frame, orderHistoryFragment, ECOSYSTEM_ORDER_HISTORY_FRAGMENT_TAG)
			.addToBackStack(null).commit();

		setVisibleScreen(ORDER_HISTORY);
	}

	@Override
	public void attachPresenter(IEcosystemPresenter presenter) {
		ecosystemPresenter = presenter;
		ecosystemPresenter.onAttach(this);
	}

	@Override
	protected void initViews() {
		findViewById(R.id.balance_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ecosystemPresenter.balanceItemClicked();
			}
		});
	}

	@Override
	public void onBackPressed() {
		ecosystemPresenter.backButtonPressed();
	}

	@Override
	public void navigateBack() {
		int count = getSupportFragmentManager().getBackStackEntryCount();
		if (count == 0) {
			super.onBackPressed();
			overridePendingTransition(0, R.anim.kinecosystem_slide_out_right);
			marketplacePresenter.backButtonPressed();
		} else {
			getSupportFragmentManager().popBackStackImmediate();
			orderHistoryPresenter.backButtonPressed();
			setVisibleScreen(MARKETPLACE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ecosystemPresenter.onDetach();
		if (balancePresenter != null) {
			balancePresenter.onDetach();
		}
		if (marketplacePresenter != null) {
			marketplacePresenter.onDetach();
		}
		if (orderHistoryPresenter != null) {
			orderHistoryPresenter.onDetach();
		}
	}
}
