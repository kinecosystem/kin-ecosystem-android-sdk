package com.kin.ecosystem.marketplace.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.network.model.Offer;
import java.util.List;

public interface IMarketplaceView extends IBaseView<MarketplacePresenter> {

    void updateSpendList(List<Offer> response);

    void updateEarnList(List<Offer> response);

    void moveToTransactionHistory();

    void showOfferActivity(String content, String offerID);

    void showSpendDialog(ISpendDialogPresenter spendDialogPresenter);

    void showToast(String msg);

    void notifyEarnItemRemoved(int index);

    void notifyEarnItemInserted(int index);

    void notifySpendItemRemoved(int index);

    void notifySpendItemInserted(int index);
}
