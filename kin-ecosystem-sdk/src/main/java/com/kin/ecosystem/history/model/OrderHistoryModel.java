package com.kin.ecosystem.history.model;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BaseModel;
import com.kin.ecosystem.network.ApiCallback;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.api.OrdersApi;
import com.kin.ecosystem.network.model.OrderList;

import java.util.List;
import java.util.Map;

public class OrderHistoryModel extends BaseModel implements IOrderHistoryModel {

    private OrdersApi ordersApi = new OrdersApi(apiClient);

    @Override
    public void getHistory(final Callback<OrderList> callback) {
        try {
            ordersApi.getHistoryAsync("", 25, "", "", new ApiCallback<OrderList>() {
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
                public void onSuccess(final OrderList result, int statusCode, Map<String, List<String>> responseHeaders) {
                    runOnMainThread(new Runnable() {
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
            e.printStackTrace();
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
        ordersApi = null;
    }
}
