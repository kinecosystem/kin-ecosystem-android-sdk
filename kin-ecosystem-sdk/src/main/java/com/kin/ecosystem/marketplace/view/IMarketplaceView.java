package com.kin.ecosystem.marketplace.view;

import com.kin.ecosystem.network.model.Offer;

import java.util.List;

public interface IMarketplaceView {

    void updateSpendList(List<Offer> response);

    void updateEarnList(List<Offer> response);

    void moveToTransactionHistory();

    void showOfferActivity(Offer offer);
}
