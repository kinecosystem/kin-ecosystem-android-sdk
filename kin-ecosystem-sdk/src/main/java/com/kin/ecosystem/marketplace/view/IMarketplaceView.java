package com.kin.ecosystem.marketplace.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.marketplace.presenter.MarketplaceViewPresenter;
import com.kin.ecosystem.network.model.Offer;

import com.kin.ecosystem.network.model.OpenOrder;
import java.util.List;

public interface IMarketplaceView extends IBaseView<MarketplaceViewPresenter> {

    void updateSpendList(List<Offer> response);

    void updateEarnList(List<Offer> response);

    void moveToTransactionHistory();

    void showOfferActivity(Offer offer);
}
