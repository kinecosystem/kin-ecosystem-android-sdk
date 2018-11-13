package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;

public interface IMarketplacePresenter extends IBasePresenter<IMarketplaceView> {

    void getOffers();

    void onItemClicked(int position, OfferType offerType);

    void showOfferActivityFailed();

    void backButtonPressed();

    INavigator getNavigator();
}
