package com.kin.ecosystem.core.data.offer;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.OfferList;


public interface OfferDataSource {

	OfferList getCachedOfferList();

	void getOffers(KinCallback<OfferList> callback);

	void addNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer);

	void removeNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer);

	ObservableData<NativeOfferClickEvent> getNativeSpendOfferObservable();

	boolean addNativeOffer(@NonNull NativeOffer nativeOffer, boolean dismissOnTap);

	boolean removeNativeOffer(@NonNull NativeOffer nativeOffer);

	boolean shouldDismissOnTap(@NonNull String offerId);

	void logout();

	interface Remote {

		void getOffers(Callback<OfferList, ApiException> callback);
	}
}
