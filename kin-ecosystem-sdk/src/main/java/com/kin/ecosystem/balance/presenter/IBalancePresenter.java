package com.kin.ecosystem.balance.presenter;

import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.IBasePresenter;


public interface IBalancePresenter extends IBasePresenter<IBalanceView> {

    void updateSubTitle(String subTitle);
}
