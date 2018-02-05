package com.kin.ecosystem.marketplace.viewmodel;


import com.kin.ecosystem.Callback;
import com.kin.ecosystem.marketplace.model.IMarketplaceModel;
import com.kin.ecosystem.marketplace.model.MarketplaceModel;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceViewModel implements IMarketplaceViewModel {

    private final IMarketplaceModel marketplaceModel = new MarketplaceModel();
    private IMarketplaceView marketView;
    private List<Offer> spendList;
    private List<Offer> earnList;

    public MarketplaceViewModel(IMarketplaceView view) {
        this.marketView = view;
        this.spendList = new ArrayList<>();
        this.earnList = new ArrayList<>();
    }

    private void splitOffersByType(List<Offer> list) {
        for (Offer offer : list) {
            if(offer.getOfferType() == Offer.OfferTypeEnum.EARN) {
                earnList.add(offer);
            }
            else{
                spendList.add(offer);
            }
        }
        marketView.updateEarnList(earnList);
        marketView.updateSpendList(spendList);
    }

    @Override
    public void onAttach() {
        getOffers();
    }

    @Override
    public void onDetach() {
        release();
    }

    private void release() {
        marketplaceModel.onDetach();
        marketView = null;
        spendList = null;
        earnList = null;
    }

    private void getOffers() {
        marketplaceModel.getOffers(new Callback<List<Offer>>() {
            @Override
            public void onResponse(List<Offer> response) {
                splitOffersByType(response);
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO show error msg
            }
        });
    }
}
