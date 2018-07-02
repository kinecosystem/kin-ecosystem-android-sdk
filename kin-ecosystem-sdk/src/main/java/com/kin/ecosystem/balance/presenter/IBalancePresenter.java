package com.kin.ecosystem.balance.presenter;

import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.main.view.EcosystemActivity.ScreenId;

public interface IBalancePresenter extends IBasePresenter<IBalanceView> {

	interface BalanceClickListener {
		void onClick();
	}

	void balanceClicked();

	void setClickListener(BalanceClickListener balanceClickListener);

	void visibleScreen(@ScreenId final int id);
}
