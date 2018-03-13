package com.kin.ecosystem.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.network.model.OfferList;

public class OfferRepository implements OfferDataSource {

    private static OfferRepository instance = null;

    private final OfferDataSource remoteData;

    private OfferList cachedOfferList;


    private OfferRepository(@NonNull OfferDataSource remoteData) {
        this.remoteData = remoteData;
    }

    public static void init(@NonNull OfferDataSource remoteData) {
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
}
