package com.kin.ecosystem.marketplace.model;

import com.kin.ecosystem.network.model.Offer;

public class NativeOffer extends Offer {

    NativeOffer(String id) {
        this.setId(id);
        this.setContentType(ContentTypeEnum.EXTERNAL);
    }
}
