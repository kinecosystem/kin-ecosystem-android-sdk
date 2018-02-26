package com.kin.ecosystem.data.offer;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.model.OfferList;

interface OfferDataSource {

    OfferList getCachedOfferList();

    void getOffers(Callback<OfferList> callback);
}
