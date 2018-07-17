package com.kin.ecosystem.main.presenter;


import static com.kin.ecosystem.main.ScreenId.MARKETPLACE;
import static com.kin.ecosystem.main.ScreenId.NONE;
import static com.kin.ecosystem.main.ScreenId.ORDER_HISTORY;
import static com.kin.ecosystem.main.Title.MARKETPLACE_TITLE;
import static com.kin.ecosystem.main.Title.ORDER_HISTORY_TITLE;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.main.ScreenId;
import com.kin.ecosystem.main.Title;
import com.kin.ecosystem.main.view.IEcosystemView;

public class EcosystemPresenter extends BasePresenter<IEcosystemView> implements IEcosystemPresenter {

	private @ScreenId int visibleScreen = NONE;
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
			navigator.navigateToMarketplace();
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
}
