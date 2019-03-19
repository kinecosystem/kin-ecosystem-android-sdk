package com.kin.ecosystem.marketplace.view;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBottomDialog;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ISpendDialog extends IBottomDialog {

    int SOMETHING_WENT_WRONG = 0x00000001;

    @IntDef({SOMETHING_WENT_WRONG})
    @Retention(RetentionPolicy.SOURCE)
    @interface Message {

    }

    void showToast(@Message final int msg);

    void showThankYouLayout(@NonNull final String title, @NonNull final String description);

    void navigateToOrderHistory();
}