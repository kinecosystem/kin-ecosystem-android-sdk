package com.kin.ecosystem.common.model;

public class NativeSpendOffer extends NativeOffer {

	public NativeSpendOffer(String id) {
		super(id);
	}

	@Override
	public OfferType getOfferType() {
		return OfferType.SPEND;
	}
}
