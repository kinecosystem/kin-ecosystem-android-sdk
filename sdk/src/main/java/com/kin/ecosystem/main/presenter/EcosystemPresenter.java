package com.kin.ecosystem.main.presenter;


import static com.kin.ecosystem.main.ScreenId.MARKETPLACE;
import static com.kin.ecosystem.main.ScreenId.NONE;
import static com.kin.ecosystem.main.ScreenId.ORDER_HISTORY;
import static com.kin.ecosystem.main.Title.MARKETPLACE_TITLE;
import static com.kin.ecosystem.main.Title.ORDER_HISTORY_TITLE;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.settings.SettingsDataSource;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.main.ScreenId;
import com.kin.ecosystem.main.Title;
import com.kin.ecosystem.main.view.IEcosystemView;
import java.math.BigDecimal;

public class EcosystemPresenter extends BasePresenter<IEcosystemView> implements IEcosystemPresenter {

	private @ScreenId
	int visibleScreen = NONE;
	private final INavigator navigator;
	private final SettingsDataSource settingsDataSource;
	private final BlockchainSource blockchainSource;

	private Observer<Balance> balanceObserver;
	private Balance currentBalance;

	public EcosystemPresenter(@NonNull IEcosystemView view, @NonNull SettingsDataSource settingsDataSource,
		@NonNull final BlockchainSource blockchainSource,
		@NonNull INavigator navigator) {
		this.view = view;
		this.settingsDataSource = settingsDataSource;
		this.blockchainSource = blockchainSource;
		this.navigator = navigator;
		this.currentBalance = blockchainSource.getBalance();

		this.view.attachPresenter(this);
	}

	@Override
	public void onAttach(IEcosystemView view) {
		super.onAttach(view);
		if (this.view != null && visibleScreen != MARKETPLACE) {
			navigator.navigateToMarketplace();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		removeBalanceObserver();
	}

	private void addBalanceObserver() {
		removeBalanceObserver();
		balanceObserver = new Observer<Balance>() {
			@Override
			public void onChanged(Balance value) {
				currentBalance = value;
				updateMenuSettingsIcon();
			}
		};
		blockchainSource.addBalanceObserver(balanceObserver);
	}

	private void updateMenuSettingsIcon() {
		if (!settingsDataSource.isBackedUp()) {
			if (currentBalance.getAmount().compareTo(BigDecimal.ZERO) == 1) {
				changeMenuTouchIndicator(true);
			} else {
				addBalanceObserver();
				changeMenuTouchIndicator(false);
			}
		} else {
			changeMenuTouchIndicator(false);
		}
	}

	private void removeBalanceObserver() {
		if (balanceObserver != null) {
			blockchainSource.removeBalanceObserver(balanceObserver);
			balanceObserver = null;
		}
	}

	@Override
	public void balanceItemClicked() {
		if (view != null && visibleScreen != ORDER_HISTORY) {
			navigator.navigateToOrderHistory(false);
		}
	}

	@Override
	public void backButtonPressed() {
		if (view != null) {
			view.navigateBack();
		}
	}

	@Override
	public void visibleScreen(@ScreenId final int id) {
		visibleScreen = id;
		@Title final int title;
		switch (id) {
			case ORDER_HISTORY:
				title = ORDER_HISTORY_TITLE;
				break;
			case MARKETPLACE:
			default:
				title = MARKETPLACE_TITLE;
				break;
		}

		if (view != null) {
			view.updateTitle(title);
		}
	}

	private void changeMenuTouchIndicator(final boolean isVisible) {
		if (view != null) {
			view.showMenuTouchIndicator(isVisible);
		}
	}

	@Override
	public void settingsMenuClicked() {
		navigator.navigateToSettings();
	}

	@Override
	public void onMenuInitialized() {
		updateMenuSettingsIcon();
	}
}
