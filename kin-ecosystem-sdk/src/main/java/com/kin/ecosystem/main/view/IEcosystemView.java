package com.kin.ecosystem.main.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.main.presenter.IEcosystemPresenter;

public interface IEcosystemView extends IBaseView<IEcosystemPresenter> {

	void updateTitle(final String title);

	void navigateToMarketplace();

	void navigateToOrderHistory(final boolean isFirstSpendOrder);

	void navigateBack();
}
