package com.kin.ecosystem.common.model;

import android.support.annotation.NonNull;

public class NativeSpendOffer extends NativeOffer {

	public NativeSpendOffer(@NonNull String id) {
		super(id);
	}

	@Override
	public OfferType getOfferType() {
		return OfferType.SPEND;
	}
}
