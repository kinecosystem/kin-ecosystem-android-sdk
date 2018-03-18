package com.kin.ecosystem.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.network.model.OfferList;

public class OfferRepository implements OfferDataSource {

    private static OfferRepository instance = null;

    private final OfferDataSource.Remote remoteData;

    private OfferList cachedOfferList;
    private ObservableData<String> pendingOfferID;

    private OfferRepository(@NonNull OfferDataSource.Remote remoteData) {
        this.remoteData = remoteData;
        this.pendingOfferID = ObservableData.create();
    }

    public static void init(@NonNull OfferDataSource.Remote remoteData) {
        if (instance == null) {
            synchronized (OfferRepository.class) {
                instance = new OfferRepository(remoteData);
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
    public ObservableData<String> getPendingOfferID() {
        return pendingOfferID;
    }

    @Override
    public void setPendingOffer(String offerID) {
        pendingOfferID.setValue(offerID);
    }
}
