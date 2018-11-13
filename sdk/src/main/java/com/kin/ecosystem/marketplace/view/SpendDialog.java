package com.kin.ecosystem.marketplace.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BottomDialog;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;


public class SpendDialog extends BottomDialog<ISpendDialogPresenter> implements ISpendDialog {

    private ImageView confirmationImage;
    private INavigator navigator;

    SpendDialog(@NonNull Context context, @NonNull INavigator navigator, @NonNull ISpendDialogPresenter presenter) {
        super(context, presenter, R.layout.kinecosystem_dialog_spend);
        this.navigator = navigator;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        presenter.dialogDismissed();
    }


    @Override
    public void showToast(@Message final int msg) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), getMessageResId(msg), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private @StringRes int getMessageResId(@Message final int msg) {
        switch (msg) {
            default:
            case SOMETHING_WENT_WRONG:
                return R.string.kinecosystem_something_went_wrong;
        }
    }

    @Override
    public void showThankYouLayout(@NonNull final String title, @NonNull final String description) {
        this.title.setText(title);
        this.description.setText(description);
        this.bottomButton.setVisibility(View.INVISIBLE);
        this.confirmationImage.setVisibility(View.VISIBLE);
        this.closeButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void navigateToOrderHistory() {
        navigator.navigateToOrderHistory(true);
    }

    @Override
    protected void initViews() {
        confirmationImage = findViewById(R.id.confirmation_image);
    }
}
