package com.kin.ecosystem.data.offer;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.OfferList;

public interface OfferDataSource {

    OfferList getCachedOfferList();

    void getOffers(Callback<OfferList> callback);
}
