package com.kin.ecosystem.core.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.model.NativeEarnOfferBuilder;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.common.model.NativeOfferBuilder;
import com.kin.ecosystem.common.model.NativeSpendOfferBuilder;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.core.network.model.Offer.OfferType;

public class OfferConverter {

	@Nullable
	public static Offer toOffer(@NonNull NativeOffer nativeOffer) {
		OfferType offerType;
		try {
			offerType = OfferType.fromValue(nativeOffer.getOfferType().getValue());
		} catch (Throwable throwable) {
			offerType = null;
		}

		if (offerType != null) {
			return new Offer().id(nativeOffer.getId())
				.offerType(offerType)
				.title(nativeOffer.getTitle())
				.description(nativeOffer.getDescription())
				.amount(nativeOffer.getAmount())
				.image(nativeOffer.getImage())
				.contentType(ContentTypeEnum.EXTERNAL);
		} else {
			return null;
		}
	}

	public static NativeOffer toNativeOffer(@NonNull Offer offer) {
		NativeOfferBuilder nativeOfferBuilder =
			(offer.getOfferType() == OfferType.EARN) ? new NativeEarnOfferBuilder(offer.getId())
				: new NativeSpendOfferBuilder(offer.getId());
		return nativeOfferBuilder.title(offer.getTitle()).description(offer.getDescription()).amount(offer.getAmount())
			.image(offer.getImage()).build();
	}
}
