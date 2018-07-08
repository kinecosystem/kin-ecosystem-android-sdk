package com.kin.ecosystem.balance.view;

import com.kin.ecosystem.balance.presenter.BalancePresenter.OrderStatus;
import com.kin.ecosystem.balance.presenter.BalancePresenter.OrderType;
import com.kin.ecosystem.balance.presenter.IBalancePresenter;
import com.kin.ecosystem.base.IBaseView;

public interface IBalanceView extends IBaseView<IBalancePresenter> {

	void updateBalance(String balance);

	void setWelcomeSubtitle();

	void updateSubTitle(final int amount, @OrderStatus final int status, @OrderType final int orderType);

	void clearSubTitle();

	void animateArrow(boolean showArrow);
}
