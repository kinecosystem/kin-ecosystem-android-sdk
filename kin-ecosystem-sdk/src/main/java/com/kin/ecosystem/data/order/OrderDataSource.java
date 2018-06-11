package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.OrderConfirmation;
import kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.OrderList;

public interface OrderDataSource {

    OrderList getAllCachedOrderHistory();

    void getAllOrderHistory(@NonNull final Callback<OrderList> callback);

    void createOrder(@NonNull final String offerID, final Callback<OpenOrder> callback);

    void submitOrder(@NonNull final String offerID, @Nullable String content, @NonNull String orderID,
        final Callback<Order> callback);

    void cancelOrder(@NonNull final String offerID, @NonNull final String orderID, final Callback<Void> callback);

    ObservableData<OpenOrder> getOpenOrder();

    void purchase(String offerJwt, @Nullable final Callback<OrderConfirmation> callback);

    void requestPayment(String offerJwt, Callback<OrderConfirmation> callback);

    void addCompletedOrderObserver(@NonNull final Observer<Order> observer);

    void removeCompletedOrderObserver(@NonNull final Observer<Order> observer);

    void isFirstSpendOrder(@NonNull final Callback<Boolean> callback);

    void setIsFirstSpendOrder(boolean isFirstSpendOrder);

    void getExternalOrderStatus(@NonNull String offerID, @NonNull final Callback<OrderConfirmation> callback);

    interface Local {

        void isFirstSpendOrder(@NonNull final Callback<Boolean> callback);

        void setIsFirstSpendOrder(boolean isFirstSpendOrder);
    }

    interface Remote {

        void getAllOrderHistory(@NonNull final Callback<OrderList> callback);

        void createOrder(@NonNull final String offerID, final Callback<OpenOrder> callback);

        void submitOrder(@Nullable String content, @NonNull String orderID, final Callback<Order> callback);

        void cancelOrder(@NonNull final String orderID, final Callback<Void> callback);

        void getOrder(String orderID, Callback<Order> callback);

        Order getOrderSync(String orderID);

        OpenOrder createExternalOrderSync(String orderJwt) throws ApiException;

        void getFilteredOrderHistory(@Nullable String origin, @NonNull String offerID, @NonNull final Callback<OrderList> callback);
    }
}
