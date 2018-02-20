package com.kin.ecosystem.marketplace.model;


import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BaseModel;
import com.kin.ecosystem.network.ApiCallback;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.api.OffersApi;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;
import java.util.List;
import java.util.Map;

public class MarketplaceModel extends BaseModel implements IMarketplaceModel {

    private OffersApi offersApi = new OffersApi(apiClient);

    @Override
    public void getOffers(final Callback<List<Offer>> callback) {
        try {
            offersApi.getOffersAsync("", 25, "", "", new ApiCallback<OfferList>() {
                @Override
                public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }

                @Override
                public void onSuccess(final OfferList result, int statusCode,
                    Map<String, List<String>> responseHeaders) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null) {
                                callback.onResponse(result.getOffers());
                            } else {
                                callback.onResponse(null);
                            }
                        }
                    });
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (final ApiException e) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(e);
                }
            });
        }
    }

    @Override
    public void release() {
        super.release();
        offersApi = null;
    }
}
