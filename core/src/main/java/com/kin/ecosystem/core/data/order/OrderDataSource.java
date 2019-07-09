package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.model.OrderConfirmation;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.Body;
import com.kin.ecosystem.core.network.model.IncomingTransfer;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.OrderList;
import com.kin.ecosystem.core.network.model.OutgoingTransfer;

public interface OrderDataSource {

    @Nullable
    OrderList getAllCachedOrderHistory();

    void getAllOrderHistory(@NonNull final KinCallback<OrderList> callback);

    void createOrder(@NonNull final String offerID, final KinCallback<OpenOrder> callback);

	void submitEarnOrder(@NonNull final String offerID, @Nullable String content, @NonNull String orderID, final String title,
		final KinCallback<Order> callback);

	void submitSpendOrder(@NonNull final String offerID, @Nullable String transaction, @NonNull String orderID, final String title,
		final KinCallback<Order> callback);

    void cancelOrderSync(@NonNull final String orderID);

    void cancelOrder(@NonNull final String offerID, @NonNull final String orderID, final KinCallback<Void> callback);

    ObservableData<OpenOrder> getOpenOrder();

    void getOrder(@NonNull final String orderID,@Nullable final KinCallback<Order> callback);

    OpenOrder createExternalOrderSync(@NonNull final String orderJwt) throws ApiException;

    OpenOrder createOutgoingTransferOrderSync(@NonNull final OutgoingTransfer payload) throws KinEcosystemException;

    OpenOrder createIncomingTransferOrderSync(@NonNull final IncomingTransfer payload) throws KinEcosystemException;

    void purchase(String offerJwt, @Nullable final KinCallback<OrderConfirmation> callback);

    void requestPayment(String offerJwt, KinCallback<OrderConfirmation> callback);

    void addOrderObserver(@NonNull final Observer<Order> observer);

    void removeOrderObserver(@NonNull final Observer<Order> observer);

    void isFirstSpendOrder(@NonNull final KinCallback<Boolean> callback);

    void setIsFirstSpendOrder(boolean isFirstSpendOrder);

    void getExternalOrderStatus(@NonNull String offerID, @NonNull final KinCallback<OrderConfirmation> callback);

	void logout();

	interface Local {

        void isFirstSpendOrder(@NonNull final Callback<Boolean, Void> callback);

        void setIsFirstSpendOrder(boolean isFirstSpendOrder);
    }

    interface Remote {

        void getAllOrderHistory(@NonNull final Callback<OrderList, ApiException> callback);

        void createOrder(@NonNull final String offerID, final Callback<OpenOrder, ApiException> callback);

        void submitEarnOrder(@Nullable String content, @NonNull String orderID, final Callback<Order, ApiException> callback);

        void submitSpendOrder(@Nullable String transaction, @NonNull String orderID, final Callback<Order, ApiException> callback);

        void cancelOrder(@NonNull final String orderID, final Callback<Void, ApiException> callback);

        void cancelOrderSync(@NonNull final String orderID);

        void getOrder(@NonNull final String orderID, Callback<Order, ApiException> callback);

        Order getOrderSync(@NonNull final String orderID);

        OpenOrder createExternalOrderSync(String orderJwt) throws ApiException;

        OpenOrder createOutgoingTransferOrderSync(@NonNull final OutgoingTransfer payload) throws ApiException;

        OpenOrder createIncomingTransferOrderSync(@NonNull final IncomingTransfer payload) throws ApiException;

        void getFilteredOrderHistory(@Nullable String origin, @NonNull String offerID, @NonNull final Callback<OrderList, ApiException> callback);

        void changeOrder(@NonNull final String orderID, @NonNull Body body, @NonNull final Callback<Order, ApiException> callback);
    }
}
