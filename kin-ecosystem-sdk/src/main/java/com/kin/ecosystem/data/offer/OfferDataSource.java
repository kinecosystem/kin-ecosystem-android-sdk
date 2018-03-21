package com.kin.ecosystem.data.offer;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;

public interface OfferDataSource {

    OfferList getCachedOfferList();

    void getOffers(Callback<OfferList> callback);

    ObservableData<Offer> getPendingOffer();

    void setPendingOfferByID(String offerID);

    interface Remote {

        void getOffers(Callback<OfferList> callback);
    }
}
