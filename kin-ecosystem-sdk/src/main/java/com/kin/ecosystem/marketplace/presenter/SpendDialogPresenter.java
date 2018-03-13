package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.marketplace.view.ISpendDialog;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.OfferInfo.Confirmation;

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
        loadInfo();
    }

    private void loadInfo() {
        if(view != null) {
            view.setupImage(offerInfo.getImage());
            view.setupTitle(offerInfo.getTitle(), offerInfo.getAmount());
            view.setupDescription(offerInfo.getDescription());
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
            Confirmation confirmation = offerInfo.getConfirmation();
            view.showThankYouLayout(confirmation.getTitle(), confirmation.getDescription());
        }
    }
}
