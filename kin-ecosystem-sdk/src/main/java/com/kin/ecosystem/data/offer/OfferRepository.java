package com.kin.ecosystem.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;

public class OfferRepository implements OfferDataSource {

    private static OfferRepository instance = null;

    private final OfferDataSource.Remote remoteData;

    private OfferList cachedOfferList;
    private ObservableData<Offer> pendingOffer = ObservableData.create();;

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
                cachedOfferList = response;
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

    @Override
    public ObservableData<Offer> getPendingOffer() {
        return pendingOffer;
    }

    @Override
    public void setPendingOfferByID(String offerID) {
        Offer offer = getCachedOfferByID(offerID);
        if(cachedOfferList != null) {
            cachedOfferList.remove(offer);
        }
        pendingOffer.postValue(offer);
    }

    @Nullable
    private Offer getCachedOfferByID(String offerID) {
        if(cachedOfferList == null) {
            return null;
        }

        return cachedOfferList.getOfferByID(offerID);
    }
}
