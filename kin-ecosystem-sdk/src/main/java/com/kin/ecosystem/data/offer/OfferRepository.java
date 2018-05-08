package com.kin.ecosystem.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.marketplace.model.NativeOffer;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.network.model.OfferList;
import java.util.Iterator;

public class OfferRepository implements OfferDataSource {

    private static OfferRepository instance = null;

    private final OfferDataSource.Remote remoteData;

    private OfferList cachedOfferList = new OfferList();
    private ObservableData<Offer> pendingOffer = ObservableData.create();

    private static Callback<NativeSpendOffer> nativeSpendOfferCallback;

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
        return cachedOfferList;
    }

    @Override
    public void getOffers(@Nullable final Callback<OfferList> callback) {
        remoteData.getOffers(new Callback<OfferList>() {
            @Override
            public void onResponse(OfferList response) {
                updateCacheOfferList(response);
                if (callback != null) {
                    callback.onResponse(cachedOfferList);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (callback != null) {
                    callback.onFailure(new DataNotAvailableException());
                }
            }
        });
    }

    private void updateCacheOfferList(OfferList response) {
        removeAllNoneNativeOffers();
        cachedOfferList.addAll(response);
    }

    private void removeAllNoneNativeOffers() {
        Iterator<Offer> offerIterator = cachedOfferList.getOffers().iterator();
        while (offerIterator.hasNext()) {
            if (offerIterator.next().getContentType() != ContentTypeEnum.EXTERNAL) {
                offerIterator.remove();
            }
        }

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

    @Override
    public void addNativeOfferCallback(Callback<NativeSpendOffer> callback) {
        nativeSpendOfferCallback = callback;
    }

    @Override
    public Callback<NativeSpendOffer> getNativeOfferCallback() {
        return nativeSpendOfferCallback;
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
    public boolean addNativeOffer(@NonNull NativeOffer nativeOffer) {
        return cachedOfferList.addAtIndex(0, nativeOffer);
    }

    @Override
    public boolean removeNativeOffer(@NonNull NativeOffer nativeOffer) {
        return cachedOfferList.remove(nativeOffer);
    }
}
