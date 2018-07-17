package com.kin.ecosystem.data.offer;

import android.support.annotation.NonNull;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.marketplace.model.NativeOffer;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;
import kin.ecosystem.core.network.ApiException;


public interface OfferDataSource {

	OfferList getCachedOfferList();

	void getOffers(KinCallback<OfferList> callback);

	void addNativeOfferClickedObserver(@NonNull Observer<NativeSpendOffer> observer);

	void removeNativeOfferClickedObserver(@NonNull Observer<NativeSpendOffer> observer);

	ObservableData<NativeSpendOffer> getNativeSpendOfferObservable();

	boolean addNativeOffer(@NonNull NativeOffer nativeOffer);

	boolean removeNativeOffer(@NonNull NativeOffer nativeOffer);

	interface Remote {

		void getOffers(Callback<OfferList, ApiException> callback);
	}
}
