package com.kin.ecosystem.balance.view;

import com.kin.ecosystem.balance.presenter.IBalancePresenter;
import com.kin.ecosystem.base.IBaseView;

public interface IBalanceView extends IBaseView<IBalancePresenter> {

    void updateBalance(String balance);

    void updateSubTitle(String subTitle);
}
