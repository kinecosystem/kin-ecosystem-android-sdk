package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.IBottomDialogPresenter;
import com.kin.ecosystem.marketplace.view.ISpendDialog;

public interface ISpendDialogPresenter extends IBottomDialogPresenter<ISpendDialog> {

    void dialogDismissed();
}
