package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.marketplace.view.ISpendDialog;

public interface ISpendDialogPresenter extends IBasePresenter<ISpendDialog> {

    void closeClicked();

    void confirmClicked();
}
