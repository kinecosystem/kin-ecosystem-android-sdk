package com.kin.ecosystem.marketplace.view;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.network.model.OfferInfo;

public interface ISpendDialog extends IBaseView<ISpendDialogPresenter> {

    void closeDialog();

    void loadInfo(@NonNull final OfferInfo info);

    void showThankYouLayout(@NonNull final OfferInfo.Confirmation confirmation);
}
