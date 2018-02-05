package com.kin.ecosystem.marketplace.model;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.ApiCallback;
import com.kin.ecosystem.network.ApiClient;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.api.DefaultApi;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketplaceModel implements IMarketplaceModel {
    private static final String TAG = MarketplaceModel.class.getSimpleName();

    private ApiClient apiClient = new ApiClient();
    private DefaultApi defaultApi = new DefaultApi(apiClient);

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void getOffers(final Callback<List<Offer>> callback) {
        ArrayList<Offer> list = new ArrayList<>();
        try {
            defaultApi.getOffersAsync(new ApiCallback<OfferList>() {
                @Override
                public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }

                @Override
                public void onSuccess(final OfferList result, int statusCode, Map<String, List<String>> responseHeaders) {
                    handler.post(new Runnable() {
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
            Log.e(TAG, "getOffers Exception: " + e.getMessage());
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(e);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        apiClient = null;
        defaultApi = null;
    }
}
