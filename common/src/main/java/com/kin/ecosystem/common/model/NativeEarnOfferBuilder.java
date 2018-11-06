package com.kin.ecosystem.common.model;

import android.support.annotation.NonNull;

public class NativeEarnOfferBuilder extends NativeOfferBuilder {

	public NativeEarnOfferBuilder(@NonNull String id) {
		nativeOffer = new NativeEarnOffer(id);
	}

}