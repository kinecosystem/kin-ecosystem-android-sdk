package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.marketplace.view.ISpendDialog;
import com.kin.ecosystem.network.model.OfferInfo;

public class SpendDialogPresenter extends BasePresenter<ISpendDialog> implements ISpendDialogPresenter {

    private OfferInfo offerInfo;
    private boolean isDismissed;

    public SpendDialogPresenter(OfferInfo offerInfo) {
        this.offerInfo = offerInfo;
    }

    @Override
    public void onAttach(ISpendDialog view) {
        super.onAttach(view);
        isDismissed = false;
        if(view != null) {
            view.loadInfo(offerInfo);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isDismissed = true;
    }

    @Override
    public void closeClicked() {
        if(view != null && !isDismissed) {
            isDismissed =  true;
            view.closeDialog();
        }
    }

    @Override
    public void confirmClicked() {
        if(view != null) {
            view.showThankYouLayout(offerInfo.getConfirmation());
        }
    }
}
