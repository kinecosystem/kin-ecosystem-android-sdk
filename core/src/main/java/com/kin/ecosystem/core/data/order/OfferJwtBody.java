package com.kin.ecosystem.core.data.order;

import com.kin.ecosystem.core.network.model.Offer.OfferType;

public class OfferJwtBody {

	private String offerId;
	private OfferType type;

	public OfferJwtBody(String offerId, OfferType type) {
		this.offerId = offerId;
		this.type = type;
	}

	public String getOfferId() {
		return offerId;
	}

	public OfferType getType() {
		return type;
	}
}
