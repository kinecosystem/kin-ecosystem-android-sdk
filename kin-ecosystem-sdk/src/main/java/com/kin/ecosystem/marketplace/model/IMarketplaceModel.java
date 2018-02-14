package com.kin.ecosystem.marketplace.model;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.IBaseModel;
import com.kin.ecosystem.network.model.Offer;

import java.util.List;

public interface IMarketplaceModel extends IBaseModel{

    void getOffers(Callback<List<Offer>> callback);
}
