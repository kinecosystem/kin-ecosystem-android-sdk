package kin.ecosystem.core.data.offer;

import android.support.annotation.NonNull;
import kin.ecosystem.common.Callback;
import kin.ecosystem.common.KinCallback;
import kin.ecosystem.common.ObservableData;
import kin.ecosystem.common.Observer;
import kin.ecosystem.common.model.NativeOffer;
import kin.ecosystem.common.model.NativeSpendOffer;
import kin.ecosystem.core.network.ApiException;
import kin.ecosystem.core.network.model.Offer;
import kin.ecosystem.core.network.model.OfferList;


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
