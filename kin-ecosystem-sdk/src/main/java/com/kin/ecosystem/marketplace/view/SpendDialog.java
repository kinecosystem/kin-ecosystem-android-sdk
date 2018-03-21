package com.kin.ecosystem.marketplace.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BottomDialog;
import com.kin.ecosystem.history.view.OrderHistoryActivity;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;


public class SpendDialog extends BottomDialog<ISpendDialogPresenter> implements ISpendDialog {

    private ImageView confirmationImage;

    public SpendDialog(@NonNull Activity activity, @NonNull ISpendDialogPresenter presenter) {
        super(activity, presenter, R.layout.dialog_spend);
        setOwnerActivity(activity);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        presenter.dialogDismissed();
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
        Activity owner = getOwnerActivity();
        if (owner != null) {
            owner.startActivity(OrderHistoryActivity.createIntent(getContext(), true));
            owner.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    protected void initViews() {
        confirmationImage = findViewById(R.id.confirmation_image);
    }
}
