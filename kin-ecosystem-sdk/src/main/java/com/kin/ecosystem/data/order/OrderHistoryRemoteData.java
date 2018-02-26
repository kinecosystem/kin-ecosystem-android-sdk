package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.ApiCallback;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.api.OrdersApi;
import com.kin.ecosystem.network.model.OrderList;
import com.kin.ecosystem.util.ExecutorsUtil;
import java.util.List;
import java.util.Map;

public class OrderHistoryRemoteData implements OrderDataSource {

    private static volatile OrderHistoryRemoteData instance;

    private final OrdersApi ordersApi;
    private final ExecutorsUtil executorsUtil;

    private OrderHistoryRemoteData(@NonNull ExecutorsUtil executorsUtil) {
        this.ordersApi = new OrdersApi();
        this.executorsUtil = executorsUtil;
    }

    public static OrderHistoryRemoteData getInstance(@NonNull ExecutorsUtil executorsUtil) {
        if (instance == null) {
            synchronized (OrderHistoryRemoteData.class) {
                instance = new OrderHistoryRemoteData(executorsUtil);
            }
        }
        return instance;
    }

    @Override
    public OrderList getAllCachedOrderHistory() {
        return null;
    }

    @Override
    public void getAllOrderHistory(@NonNull final Callback<OrderList> callback) {
        try {
            ordersApi.getHistoryAsync("", 25, "", "", new ApiCallback<OrderList>() {
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
                public void onSuccess(final OrderList result, int statusCode,
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
