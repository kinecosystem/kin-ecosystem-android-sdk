package com.kin.ecosystem.common.model;

import android.support.annotation.NonNull;

public class NativeEarnOffer extends NativeOffer {

	public NativeEarnOffer(@NonNull String id) {
		super(id);
	}

	@Override
	public OfferType getOfferType() {
		return OfferType.EARN;
	}
}
