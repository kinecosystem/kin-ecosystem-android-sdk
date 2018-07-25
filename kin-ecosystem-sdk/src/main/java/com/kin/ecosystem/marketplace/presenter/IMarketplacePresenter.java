package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer.OfferType;

public interface IMarketplacePresenter extends IBasePresenter<IMarketplaceView> {

    void getOffers();

    void onItemClicked(int position, OfferType offerType);

    void showOfferActivityFailed();

    void backButtonPressed();

    INavigator getNavigator();
}
