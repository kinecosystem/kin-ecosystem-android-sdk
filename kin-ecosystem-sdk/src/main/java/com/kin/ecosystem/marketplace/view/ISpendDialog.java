package com.kin.ecosystem.marketplace.view;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.network.model.OfferInfo;

public interface ISpendDialog extends IBaseView<ISpendDialogPresenter> {

    void closeDialog();

    void setupImage(String image);

    void setupTitle(String titleText, int amount);

    void setupDescription(String descriptionText);

    void showThankYouLayout(@NonNull final String title, @NonNull final String description);

    void showToast(String msg);
}