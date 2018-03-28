package com.kin.ecosystem.balance.view;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.base.IBaseView;

public interface IBalanceView extends IBaseView<IBasePresenter<IBalanceView>> {

    void updateBalance(String balance);

    void updateSubTitle(String subTitle);
}
