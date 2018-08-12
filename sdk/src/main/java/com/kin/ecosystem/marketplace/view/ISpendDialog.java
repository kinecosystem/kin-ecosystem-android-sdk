package com.kin.ecosystem.marketplace.view;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBottomDialog;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;

public interface ISpendDialog extends IBottomDialog<ISpendDialogPresenter> {

    void showThankYouLayout(@NonNull final String title, @NonNull final String description);

    void navigateToOrderHistory();
}