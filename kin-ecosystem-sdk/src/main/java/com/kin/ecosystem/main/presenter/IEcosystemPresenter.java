package com.kin.ecosystem.main.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.main.view.EcosystemActivity.ScreenId;
import com.kin.ecosystem.main.view.IEcosystemView;

public interface IEcosystemPresenter extends IBasePresenter<IEcosystemView> {

	void balanceItemClicked();

	void backButtonPressed();

	void visibleScreen(@ScreenId final int id);
}
