package com.kin.ecosystem.common.model;

public abstract class NativeOfferBuilder {

	protected NativeOffer nativeOffer;


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

	public NativeOfferBuilder dismissOnTap(boolean dismissOnTap) {
		nativeOffer.setDismissOnTap(dismissOnTap);
		return this;
	}

	public NativeOffer build() {
		return nativeOffer;
	}
}