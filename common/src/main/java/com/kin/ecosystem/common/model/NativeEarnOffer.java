package com.kin.ecosystem.common.model;

public class NativeEarnOffer extends NativeOffer {

	public NativeEarnOffer(String id) {
		super(id);
	}

	@Override
	public OfferType getOfferType() {
		return OfferType.EARN;
	}
}
