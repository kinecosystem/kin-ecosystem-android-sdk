package com.kin.ecosystem.marketplace.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer.OfferTypeEnum;

public interface IMarketplaceViewPresenter extends IBasePresenter<IMarketplaceView> {

    void onItemClicked(int position, OfferTypeEnum offerType);
}
