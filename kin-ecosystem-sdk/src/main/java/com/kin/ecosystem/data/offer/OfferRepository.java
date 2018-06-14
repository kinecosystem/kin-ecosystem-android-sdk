package com.kin.ecosystem.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.marketplace.model.NativeOffer;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;
import com.kin.ecosystem.util.ErrorUtil;

public class OfferRepository implements OfferDataSource {

    private static OfferRepository instance = null;

    private final OfferDataSource.Remote remoteData;

    private OfferList nativeOfferList = new OfferList();
    private OfferList cachedOfferList = new OfferList();
    private ObservableData<Offer> pendingOffer = ObservableData.create();

    private ObservableData<NativeSpendOffer> nativeSpendOfferObservable = ObservableData.create();

    private OfferRepository(@NonNull OfferDataSource.Remote remoteData) {
        this.remoteData = remoteData;
    }

    public static void init(@NonNull OfferDataSource.Remote remoteData) {
        if (instance == null) {
            synchronized (OfferRepository.class) {
                if (instance == null) {
                    instance = new OfferRepository(remoteData);
                }
            }
        }
    }

    public static OfferRepository getInstance() {
        return instance;
    }

    @Override
    public OfferList getCachedOfferList() {
        return getList();
    }

    @Override
    public void getOffers(@Nullable final KinCallback<OfferList> callback) {
        remoteData.getOffers(new Callback<OfferList, ApiException>() {
            @Override
            public void onResponse(OfferList response) {
                cachedOfferList = response;
                if (callback != null) {
                    callback.onResponse(getList());
                }
            }

            @Override
            public void onFailure(ApiException e) {
                if (callback != null) {
                    callback.onFailure(ErrorUtil.fromApiException(e));
                }
            }

        });
    }

    private OfferList getList() {
        OfferList masterList = new OfferList();
        masterList.addAll(nativeOfferList);
        masterList.addAll(cachedOfferList);
        masterList.setPaging(cachedOfferList.getPaging());
        return masterList;
    }

    @Override
    public ObservableData<Offer> getPendingOffer() {
        return pendingOffer;
    }

    @Override
    public void setPendingOfferByID(String offerID) {
        Offer offer = getCachedOfferByID(offerID);
        removeFromCachedOfferList(offer);
        pendingOffer.postValue(offer);
    }

    private void removeFromCachedOfferList(Offer offer) {
        if (cachedOfferList != null) {
            cachedOfferList.remove(offer);
        }
    }

    @Nullable
    private Offer getCachedOfferByID(String offerID) {
        if (cachedOfferList == null) {
            return null;
        }

        return cachedOfferList.getOfferByID(offerID);
    }

    @Override
    public void addNativeOfferClickedObserver(@NonNull Observer<NativeSpendOffer> observer) {
        nativeSpendOfferObservable.addObserver(observer);
    }

    @Override
    public void removeNativeOfferClickedObserver(@NonNull Observer<NativeSpendOffer> observer) {
        nativeSpendOfferObservable.removeObserver(observer);
    }

    @Override
    public ObservableData<NativeSpendOffer> getNativeSpendOfferObservable() {
        return nativeSpendOfferObservable;
    }

    @Override
    public boolean addNativeOffer(@NonNull NativeOffer nativeOffer) {
        Offer offer = nativeOfferList.getOfferByID(nativeOffer.getId());
        if (offer == null) {
            return nativeOfferList.addAtIndex(0, nativeOffer);
        }
        return false;
    }

    @Override
    public boolean removeNativeOffer(@NonNull NativeOffer nativeOffer) {
        return nativeOfferList.remove(nativeOffer);
    }
}
