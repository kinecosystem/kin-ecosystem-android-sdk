package com.kin.ecosystem.marketplace.presenter;


import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceViewPresenter implements IBasePresenter {

    private final OfferRepository offerRepository;
    private IMarketplaceView marketView;
    private List<Offer> spendList;
    private List<Offer> earnList;

    public MarketplaceViewPresenter(IMarketplaceView view) {
        this.marketView = view;
        this.spendList = new ArrayList<>();
        this.earnList = new ArrayList<>();
        this.offerRepository = OfferRepository.getInstance();
    }

    private void splitOffersByType(List<Offer> list) {
        for (Offer offer : list) {
            if (offer.getOfferType() == Offer.OfferTypeEnum.EARN) {
                earnList.add(offer);
            } else {
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
        marketView = null;
        spendList = null;
        earnList = null;
    }

    private void getOffers() {
        OfferList cachedOfferList = offerRepository.getCachedOfferList();
        if (cachedOfferList != null) {
            splitOffersByType(cachedOfferList.getOffers());
        }

        offerRepository.getOffers(new Callback<OfferList>() {
            @Override
            public void onResponse(OfferList offerList) {
                splitOffersByType(offerList.getOffers());
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO show error msg
            }
        });
    }
}
