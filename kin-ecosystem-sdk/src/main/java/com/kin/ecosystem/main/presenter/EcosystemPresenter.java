package com.kin.ecosystem.main.presenter;

import static com.kin.ecosystem.main.view.EcosystemActivity.MARKETPLACE;
import static com.kin.ecosystem.main.view.EcosystemActivity.ORDER_HISTORY;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.main.view.EcosystemActivity.ScreenId;
import com.kin.ecosystem.main.view.IEcosystemView;

public class EcosystemPresenter extends BasePresenter<IEcosystemView> implements IEcosystemPresenter {

	private static final String KIN_MARKETPLACE_BETA_TITLE = "Kin Marketplace (Beta)";
	private static final String ORDER_HISTORY_TITLE = "Transaction History";

	private @ScreenId int visibleScreen;
	private final INavigator navigator;

	public EcosystemPresenter(@NonNull IEcosystemView view, @NonNull INavigator navigator) {
		this.view = view;
		this.navigator = navigator;
		this.view.attachPresenter(this);
	}

	@Override
	public void onAttach(IEcosystemView view) {
		super.onAttach(view);
		if (this.view != null && visibleScreen != MARKETPLACE) {
			this.view.navigateToMarketplace();
		}
	}

	@Override
	public void balanceItemClicked() {
		if (view != null && visibleScreen != ORDER_HISTORY) {
			view.navigateToOrderHistory(false);
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
		String title;
		switch (id) {
			case ORDER_HISTORY:
				title = ORDER_HISTORY_TITLE;
				break;
			case MARKETPLACE:
			default:
				title = KIN_MARKETPLACE_BETA_TITLE;
				break;
		}

		if (view != null) {
			view.updateTitle(title);
		}
	}
}
