package com.kin.ecosystem.marketplace.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.Offer;
import java.util.List;

public interface IMarketplaceView extends IBaseView<MarketplacePresenter> {

    void updateSpendList(List<Offer> response);

    void updateEarnList(List<Offer> response);

    void moveToTransactionHistory();

    void showOfferActivity(Offer offer);

    void showSpendDialog(OfferInfo offerInfo);

    void showToast(String msg);
}
