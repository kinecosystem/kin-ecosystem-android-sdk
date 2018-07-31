package kin.ecosystem.core.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import kin.ecosystem.common.model.NativeOffer;
import kin.ecosystem.common.model.NativeSpendOffer;
import kin.ecosystem.core.network.model.Offer;
import kin.ecosystem.core.network.model.Offer.ContentTypeEnum;
import kin.ecosystem.core.network.model.Offer.OfferType;

public class Converter {

	@Nullable
	public static Offer toOffer(@NonNull NativeOffer nativeOffer){
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

	public static NativeSpendOffer toNativeSpendOffer(Offer offer) {
		if(offer.getOfferType() == OfferType.SPEND) {
			return new NativeSpendOffer(offer.getId())
				.title(offer.getTitle())
				.description(offer.getDescription())
				.amount(offer.getAmount())
				.image(offer.getImage());
		} else {
			return null;
		}
	}
}
