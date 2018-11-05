package com.kin.ecosystem.common.model;

import android.support.annotation.NonNull;

public class NativeSpendOfferBuilder extends NativeOfferBuilder {

	public NativeSpendOfferBuilder(@NonNull String id){
		nativeOffer = new NativeSpendOffer(id);
	}

}
