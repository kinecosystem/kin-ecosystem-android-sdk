package com.kin.ecosystem.common.model;

import com.kin.ecosystem.common.model.NativeOffer.OfferType;

public class NativeOfferBuilder {

    private NativeOffer nativeOffer;

    public NativeOfferBuilder(String id) {
        nativeOffer = new NativeOffer(id);
    }

    public NativeOfferBuilder offerType(OfferType offerType) {
        nativeOffer.setOfferType(offerType);
        return this;
    }

    public NativeOfferBuilder title(String title) {
        nativeOffer.setTitle(title);
        return this;
    }

    public NativeOfferBuilder description(String description) {
        nativeOffer.setDescription(description);
        return this;
    }

    public NativeOfferBuilder amount(Integer amount) {
        nativeOffer.setAmount(amount);
        return this;
    }

    public NativeOfferBuilder image(String image) {
        nativeOffer.setImage(image);
        return this;
    }

    public NativeOffer build()
    {
        return nativeOffer;
    }
}
