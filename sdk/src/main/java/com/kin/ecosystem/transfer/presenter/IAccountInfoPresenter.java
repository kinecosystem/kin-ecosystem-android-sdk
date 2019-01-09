package com.kin.ecosystem.transfer.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.transfer.view.IAccountInfoView;

public interface IAccountInfoPresenter extends IBasePresenter<IAccountInfoView> {
    void agreeClicked();
    void backButtonPressed();
    void xCloseClicked();
    void startInit();
    void onError();
}
