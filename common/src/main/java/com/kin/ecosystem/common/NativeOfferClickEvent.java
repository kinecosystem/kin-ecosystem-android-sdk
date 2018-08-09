package com.kin.ecosystem.common;

import com.kin.ecosystem.common.model.NativeOffer;

public class NativeOfferClickEvent {

	/**
	 * The native offer that was clicked.
	 */
	private NativeOffer nativeOffer;

	/**
	 * True if the marketplace was dismissed.
	 */
	private boolean isDismissOnTap;

	private NativeOfferClickEvent(NativeOffer nativeOffer, boolean isDismissOnTap) {
		this.nativeOffer = nativeOffer;
		this.isDismissOnTap = isDismissOnTap;
	}

	public NativeOffer getNativeOffer() {
		return nativeOffer;
	}

	public boolean isDismissOnTap() {
		return isDismissOnTap;
	}

	public static class Builder {

		private NativeOffer nativeOffer;
		private boolean isDismissed;

		public Builder nativeOffer(NativeOffer nativeOffer) {
			this.nativeOffer = nativeOffer;
			return this;
		}

		public Builder isDismissed(boolean isDismissed) {
			this.isDismissed = isDismissed;
			return this;
		}

		public NativeOfferClickEvent build() {
			if(nativeOffer == null) {
				throw new IllegalArgumentException("NativeOffer can't be null");
			}
			return new NativeOfferClickEvent(nativeOffer, isDismissed);
		}

	}
}
