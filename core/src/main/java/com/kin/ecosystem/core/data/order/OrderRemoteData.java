package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.network.ApiCallback;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.api.OrdersApi;
import com.kin.ecosystem.core.network.model.Body;
import com.kin.ecosystem.core.network.model.EarnSubmission;
import com.kin.ecosystem.core.network.model.ExternalOrderRequest;
import com.kin.ecosystem.core.network.model.IncomingTransfer;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.OrderList;
import com.kin.ecosystem.core.network.model.OutgoingTransfer;
import com.kin.ecosystem.core.network.model.SpendOrderPayload;
import com.kin.ecosystem.core.util.ExecutorsUtil;
import java.util.List;
import java.util.Map;

public class OrderRemoteData implements OrderDataSource.Remote {

    private static final String TAG = OrderRemoteData.class.getSimpleName();

    private static final int ORDERS_ITEMS_LIMIT = 100;
    private static final int ONE_ORDER_LIMIT = 1;

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
                if (instance == null) {
                    instance = new OrderRemoteData(executorsUtil);
                }
            }
        }
        return instance;
    }

    @Override
    public void getAllOrderHistory(@NonNull final Callback<OrderList, ApiException> callback) {
        getHistory(null, null, ORDERS_ITEMS_LIMIT, callback);
    }

    @Override
    public void createOrder(@NonNull final String offerID, @NonNull final Callback<OpenOrder, ApiException> callback) {
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
    public void submitEarnOrder(@NonNull String content, @NonNull String orderID, @NonNull final Callback<Order, ApiException> callback) {
        try {
            ordersApi.submitEarnOrderAsync(new EarnSubmission().content(content), orderID, "", new ApiCallback<Order>() {
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
    public void submitSpendOrder(@NonNull String transaction, @NonNull String orderID, @NonNull final Callback<Order, ApiException> callback) {
		try {
			ordersApi.submitSpendOrderAsync(new SpendOrderPayload().transaction(transaction), orderID, "", new ApiCallback<Order>() {
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
    public void cancelOrder(@NonNull final String orderID, @Nullable final Callback<Void, ApiException> callback) {
        try {
            ordersApi.cancelOrderAsync(orderID, "", new ApiCallback<Void>() {
                @Override
                public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onFailure(e);
                            }
                        }
                    });
                }

                @Override
                public void onSuccess(final Void result, int statusCode, Map<String, List<String>> responseHeaders) {
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onResponse(result);
                            }
                        }
                    });
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
    public void cancelOrderSync(@NonNull String orderID) {
        try {
            ordersApi.cancelOrder(orderID, "");
        } catch (ApiException e) {
			Logger
                .log(new Log().withTag(TAG).priority(Log.ERROR).put("Cancel order", orderID).put("sync failed, code", e.getCode()));
        }
    }

    @Override
    public void getOrder(@NonNull final String orderID, final Callback<Order, ApiException> callback) {
        new GetOrderPollingCall(this, orderID, new Callback<Order, ApiException>() {
            @Override
            public void onResponse(final Order result) {
                executorsUtil.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(result);
                    }
                });
            }

            @Override
            public void onFailure(final ApiException e) {
                executorsUtil.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }
        }).start();
    }

    @Override
    public Order getOrderSync(@NonNull final String orderID) {
        Order order = null;
        try {
            order = ordersApi.getOrder(orderID, "");
        } catch (ApiException e) {
			Logger.log(new Log().withTag(TAG).priority(Log.ERROR).put("Get order", orderID).put("sync failed, code",e.getCode()));
        }
        return order;
    }

    public OpenOrder createExternalOrderSync(String orderJwt) throws ApiException {
        return ordersApi.createExternalOrder(new ExternalOrderRequest().jwt(orderJwt), "");
    }

    @Override
    public OpenOrder createOutgoingTransferOrderSync(@NonNull final OutgoingTransfer payload) throws ApiException {
        return ordersApi.createOutgoingTransferOrder(payload, "");
    }

    @Override
    public OpenOrder createIncomingTransferOrderSync(@NonNull IncomingTransfer payload) throws ApiException {
        return ordersApi.createIncomingTransferOrder(payload, "");
    }

    @Override
    public void getFilteredOrderHistory(@Nullable String origin, @NonNull String offerID,
        @NonNull Callback<OrderList, ApiException> callback) {
        getHistory(origin, offerID, ONE_ORDER_LIMIT, callback);
    }

    @Override
    public void changeOrder(@NonNull final String orderID, @NonNull Body body, @NonNull final Callback<Order, ApiException> callback) {
        try {
            ordersApi.changeOrderAsync(orderID, body, new ApiCallback<Order>() {
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

    private void getHistory(@Nullable String origin, @Nullable String offerID, int limit,
        @NonNull final Callback<OrderList, ApiException> callback) {
        try {
            ordersApi.getHistoryAsync("", origin, offerID, limit, null, null, new ApiCallback<OrderList>() {
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
