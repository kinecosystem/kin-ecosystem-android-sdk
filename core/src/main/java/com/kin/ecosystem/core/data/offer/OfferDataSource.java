package com.kin.ecosystem.core.data.offer;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.Subscription;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.OfferList;
import java.util.List;


public interface OfferDataSource {

	OfferList getCachedOfferList();

	void getOffers(KinCallback<OfferList> callback);

	void addNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer);

	void removeNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer);

	ObservableData<NativeOfferClickEvent> getNativeSpendOfferObservable();

	Subscription<Offer> addNativeOfferRemovedObserver(@NonNull Observer<Offer> observer);

	boolean addNativeOffer(@NonNull NativeOffer nativeOffer);

	boolean addAllNativeOffers(List<NativeOffer> nativeOfferList);

	boolean removeNativeOffer(@NonNull NativeOffer nativeOffer);

	void logout();


	interface Remote {

		void getOffers(Callback<OfferList, ApiException> callback);
	}
}
