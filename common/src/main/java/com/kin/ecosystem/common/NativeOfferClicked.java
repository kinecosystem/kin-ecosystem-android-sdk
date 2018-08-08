package com.kin.ecosystem.common;

import com.kin.ecosystem.common.model.NativeOffer;

public class NativeOfferClicked {

	/**
	 * The native offer that was clicked.
	 */
	private NativeOffer nativeOffer;

	/**
	 * True if the marketplace was dismissed.
	 */
	private boolean isDismissed;

	private NativeOfferClicked(NativeOffer nativeOffer, boolean isDismissed) {
		this.nativeOffer = nativeOffer;
		this.isDismissed = isDismissed;
	}

	public NativeOffer getNativeOffer() {
		return nativeOffer;
	}

	public boolean isDismissed() {
		return isDismissed;
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

		public NativeOfferClicked build() {
			if(nativeOffer == null) {
				throw new IllegalArgumentException("nativeOffer can't be null");
			}
			return new NativeOfferClicked(nativeOffer, isDismissed);
		}

	}
}
