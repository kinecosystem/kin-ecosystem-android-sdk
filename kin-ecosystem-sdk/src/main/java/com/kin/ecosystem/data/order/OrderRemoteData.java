package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.network.ApiCallback;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.api.OrdersApi;
import com.kin.ecosystem.network.model.EarnSubmission;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.OrderList;
import com.kin.ecosystem.util.ExecutorsUtil;
import java.util.List;
import java.util.Map;

public class OrderRemoteData implements OrderDataSource {

    private static final int ORDERS_ITEMS_LIMIT = 100;
    private static volatile OrderRemoteData instance;

    private final OrdersApi ordersApi;
    private final ExecutorsUtil executorsUtil;

    private OrderRemoteData(@NonNull ExecutorsUtil executorsUtil) {
        this.ordersApi = new OrdersApi();
        this.executorsUtil = executorsUtil;
    }

    public static OrderRemoteData getInstance(@NonNull ExecutorsUtil executorsUtil) {
        if (instance == null) {
            synchronized (OrderRemoteData.class) {
                instance = new OrderRemoteData(executorsUtil);
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
            ordersApi.getHistoryAsync("", ORDERS_ITEMS_LIMIT, "", "", new ApiCallback<OrderList>() {
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

    @Override
    public ObservableData<OpenOrder> getOpenOrder() {
        return null;
    }

    public void createOrder(@NonNull final String offerID, @NonNull final Callback<OpenOrder> callback) {
        try {
            ordersApi.createOrderAsync(offerID, "", new ApiCallback<OpenOrder>() {
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
                public void onSuccess(final OpenOrder result, int statusCode,
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

    public void submitOrder(@NonNull String content, @NonNull String orderID, @NonNull final Callback<Order> callback) {
        try {
            ordersApi.submitOrderAsync(new EarnSubmission().content(content), orderID, "", new ApiCallback<Order>() {
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
                public void onSuccess(final Order result, int statusCode, Map<String, List<String>> responseHeaders) {
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

    public void cancelOrder(@NonNull final String orderID, @NonNull final Callback<Void> callback) {
        try {
            ordersApi.cancelOrderAsync(orderID, "", new ApiCallback<Void>() {
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
                public void onSuccess(final Void result, int statusCode, Map<String, List<String>> responseHeaders) {
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
