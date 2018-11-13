package com.kin.ecosystem.core.data.offer;

import android.support.annotation.NonNull;
import com.kin.ecosystem.core.network.ApiException;
import java.util.List;
import java.util.Map;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.core.network.ApiCallback;
import com.kin.ecosystem.core.network.api.OffersApi;
import com.kin.ecosystem.core.network.model.OfferList;
import com.kin.ecosystem.core.util.ExecutorsUtil;

public class OfferRemoteData implements OfferDataSource.Remote {

    private static volatile OfferRemoteData instance;

    private final OffersApi offersApi;
    private final ExecutorsUtil executorsUtil;

    private OfferRemoteData(@NonNull ExecutorsUtil executorsUtil) {
        this.offersApi = new OffersApi();
        this.executorsUtil = executorsUtil;
    }

    public static OfferRemoteData getInstance(@NonNull ExecutorsUtil executorsUtil) {
        if (instance == null) {
            synchronized (OfferRemoteData.class) {
                if (instance == null) {
                    instance = new OfferRemoteData(executorsUtil);
                }
            }
        }
        return instance;
    }

    @Override
    public void getOffers(@NonNull final Callback<OfferList, ApiException> callback) {
        try {
            offersApi.getOffersAsync("", 25, "", "", new ApiCallback<OfferList>() {
                @Override
                public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }

                @Override
                public void onSuccess(final OfferList result, int statusCode,
                    Map<String, List<String>> responseHeaders) {
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(result);

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
            executorsUtil.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(e);
                }
            });
        }
    }
}
