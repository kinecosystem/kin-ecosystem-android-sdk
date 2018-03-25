package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer.OfferTypeEnum;

public interface IMarketplacePresenter extends IBasePresenter<IMarketplaceView> {

    void getOffers();

    void onItemClicked(int position, OfferTypeEnum offerType);
}
