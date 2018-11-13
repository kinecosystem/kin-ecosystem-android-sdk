package com.kin.ecosystem.main.view;

import static com.kin.ecosystem.main.ScreenId.MARKETPLACE;
import static com.kin.ecosystem.main.ScreenId.ORDER_HISTORY;
import static com.kin.ecosystem.main.Title.MARKETPLACE_TITLE;
import static com.kin.ecosystem.main.Title.ORDER_HISTORY_TITLE;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.kin.ecosystem.R;
import com.kin.ecosystem.balance.presenter.BalancePresenter;
import com.kin.ecosystem.balance.presenter.IBalancePresenter;
import com.kin.ecosystem.balance.presenter.IBalancePresenter.BalanceClickListener;
import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.core.bi.EventLoggerImpl;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.offer.OfferRepository;
import com.kin.ecosystem.core.data.order.OrderRepository;
import com.kin.ecosystem.core.data.settings.SettingsDataSourceImpl;
import com.kin.ecosystem.core.data.settings.SettingsDataSourceLocal;
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter;
import com.kin.ecosystem.history.view.OrderHistoryFragment;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.main.ScreenId;
import com.kin.ecosystem.main.Title;
import com.kin.ecosystem.main.presenter.EcosystemPresenter;
import com.kin.ecosystem.main.presenter.IEcosystemPresenter;
import com.kin.ecosystem.marketplace.presenter.IMarketplacePresenter;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.marketplace.view.MarketplaceFragment;
import com.kin.ecosystem.settings.view.SettingsActivity;


public class EcosystemActivity extends BaseToolbarActivity implements IEcosystemView, INavigator {

	public static final String ECOSYSTEM_MARKETPLACE_FRAGMENT_TAG = "ecosystem_marketplace_fragment_tag";
	public static final String ECOSYSTEM_ORDER_HISTORY_FRAGMENT_TAG = "ecosystem_order_history_fragment_tag";
	public static final int ALPHA_255 = 255;
	public static final int ALPHA_0 = 0;

	private IBalancePresenter balancePresenter;
	private IEcosystemPresenter ecosystemPresenter;
	private IMarketplacePresenter marketplacePresenter;

	private Menu menu;

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
		return R.drawable.kinecosystem_ic_back_black;
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
		balancePresenter = new BalancePresenter(balanceView, EventLoggerImpl.getInstance(),
			BlockchainSourceImpl.getInstance(), OrderRepository.getInstance());
		balancePresenter.setClickListener(new BalanceClickListener() {
			@Override
			public void onClick() {
				ecosystemPresenter.balanceItemClicked();
			}
		});
		ecosystemPresenter = new EcosystemPresenter(this,
			new SettingsDataSourceImpl(new SettingsDataSourceLocal(getApplicationContext())),
			BlockchainSourceImpl.getInstance(), this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ecosystemPresenter.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.kinecosystem_menu_marketplace, menu);
		this.menu = menu;
		ecosystemPresenter.onMenuInitialized();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		int itemId = item.getItemId();
		if (itemId == R.id.menu_settings) {
			ecosystemPresenter.settingsMenuClicked();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void showMenuTouchIndicator(boolean isVisible) {
		if (menu != null) {
			final MenuItem settingsItem = menu.findItem(R.id.menu_settings);
			if (settingsItem != null) {
				LayerDrawable icon = (LayerDrawable) settingsItem.getIcon();
				Drawable touchIndicator = icon.findDrawableByLayerId(R.id.ic_info_dot);
				if (isVisible) {
					touchIndicator.setAlpha(ALPHA_255);
				} else {
					touchIndicator.setAlpha(ALPHA_0);
				}
			}
		}
	}

	@Override
	public void updateTitle(@Title final int title) {
		final int titleResId;
		switch (title) {
			case ORDER_HISTORY_TITLE:
				titleResId = R.string.kinecosystem_transaction_history;
				break;
			case MARKETPLACE_TITLE:
			default:
				titleResId = R.string.kinecosystem_kin_marketplace;
				break;
		}
		getToolbar().setTitle(titleResId);
	}

	@Override
	public void navigateToMarketplace() {
		MarketplaceFragment marketplaceFragment = (MarketplaceFragment) getSupportFragmentManager()
			.findFragmentByTag(ECOSYSTEM_MARKETPLACE_FRAGMENT_TAG);
		if (marketplaceFragment == null) {
			marketplaceFragment = MarketplaceFragment.newInstance();
		}

		marketplacePresenter = getMarketplacePresenter(marketplaceFragment);

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, marketplaceFragment, ECOSYSTEM_MARKETPLACE_FRAGMENT_TAG)
			.commit();

		setVisibleScreen(MARKETPLACE);
	}

	private IMarketplacePresenter getMarketplacePresenter(MarketplaceFragment marketplaceFragment) {
		if (marketplacePresenter == null) {
			marketplacePresenter = new MarketplacePresenter(marketplaceFragment, OfferRepository.getInstance(),
				OrderRepository.getInstance(),
				BlockchainSourceImpl.getInstance(),
				this,
				EventLoggerImpl.getInstance());
		}

		return marketplacePresenter;
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

		new OrderHistoryPresenter(orderHistoryFragment,
			OrderRepository.getInstance(),
			BlockchainSourceImpl.getInstance(),
			EventLoggerImpl.getInstance(),
			isFirstSpendOrder);

		getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(
				R.anim.kinecosystem_slide_in_right,
				R.anim.kinecosystem_slide_out_left,
				R.anim.kinrecovery_slide_in_left,
				R.anim.kinecosystem_slide_out_right)
			.replace(R.id.fragment_frame, orderHistoryFragment, ECOSYSTEM_ORDER_HISTORY_FRAGMENT_TAG)
			.addToBackStack(null).commit();

		setVisibleScreen(ORDER_HISTORY);
	}

	@Override
	public void navigateToSettings() {
		Intent settingsIntent = new Intent(this, SettingsActivity.class);
		startActivity(settingsIntent);
		overridePendingTransition(R.anim.kinecosystem_slide_in_right, R.anim.kinecosystem_slide_out_left);
	}

	@Override
	public void close() {
		finish();
	}

	@Override
	public void attachPresenter(IEcosystemPresenter presenter) {
		ecosystemPresenter = presenter;
		ecosystemPresenter.onAttach(this);
	}

	@Override
	protected void initViews() {

	}

	@Override
	public void onBackPressed() {
		ecosystemPresenter.backButtonPressed();
	}

	@Override
	public void navigateBack() {
		int count = getSupportFragmentManager().getBackStackEntryCount();
		if (count == 0) {
			marketplacePresenter.backButtonPressed();
			super.onBackPressed();
			overridePendingTransition(0, R.anim.kinecosystem_slide_out_right);
		} else {
			getSupportFragmentManager().popBackStackImmediate();
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
	}
}
