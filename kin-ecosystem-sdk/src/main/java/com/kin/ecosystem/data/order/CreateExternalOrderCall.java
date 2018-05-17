package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import android.util.Log;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.network.model.Offer.OfferType;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.util.ExecutorsUtil.MainThreadExecutor;
import java.math.BigDecimal;

abstract class CreateExternalOrderCall extends Thread {

    private static final String TAG = CreateExternalOrderCall.class.getSimpleName();

    private final OrderDataSource.Remote remote;
    private final IBlockchainSource blockchainSource;
    private final String orderJwt;
    private final ExternalOrderCallbacks externalOrderCallbacks;

    private OpenOrder openOrder;
    private MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

    abstract OfferType getOfferType();

    public CreateExternalOrderCall(@NonNull OrderDataSource.Remote remote, @NonNull IBlockchainSource blockchainSource,
        @NonNull String orderJwt,
        @NonNull ExternalOrderCallbacks externalOrderCallbacks) {
        this.remote = remote;
        this.blockchainSource = blockchainSource;
        this.orderJwt = orderJwt;
        this.externalOrderCallbacks = externalOrderCallbacks;
    }

    @Override
    public void run() {
        try {
            // Create external order
            openOrder = remote.createExternalOrderSync(orderJwt);
            if (getOfferType() == OfferType.EARN) {
                ((ExternalEarnOrderCallbacks) externalOrderCallbacks).onOrderCreated(openOrder);
            }
        } catch (final ApiException e) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        externalOrderCallbacks.onOrderFailed(e.getResponseBody().getError());
                    } catch (Exception e) {
                        externalOrderCallbacks.onOrderFailed("Could not create order");
                    }
                }
            });
            return;
        }

        if (getOfferType() == OfferType.SPEND) {
            // Send transaction to the network.
            blockchainSource.sendTransaction(openOrder.getBlockchainData().getRecipientAddress(),
                new BigDecimal(openOrder.getAmount()), openOrder.getId());

            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    ((ExternalSpendOrderCallbacks) externalOrderCallbacks).onTransactionSent(openOrder);
                }
            });
        }

        //Listen for payments, make sure the transaction succeed.
        blockchainSource.addPaymentObservable(new Observer<Payment>() {
            @Override
            public void onChanged(final Payment payment) {
                Log.d(TAG,
                    "addPaymentObservable onChanged: " + payment.getOrderID() + " isSucceed: " + payment.isSucceed());
                if (isPaymentOrderEquals(payment, openOrder.getId())) {
                    if (payment.isSucceed()) {
                        getOrder(payment.getOrderID());
                    } else {
                        if (externalOrderCallbacks instanceof ExternalSpendOrderCallbacks) {
                            runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ExternalSpendOrderCallbacks) externalOrderCallbacks)
                                        .onTransactionFailed(openOrder, payment.getResultMessage());
                                }
                            });
                        }
                    }
                    blockchainSource.removePaymentObserver(this);
                }
            }
        });
    }

    private boolean isPaymentOrderEquals(Payment payment, String orderId) {
        String paymentOrderID = payment.getOrderID();
        return paymentOrderID != null && paymentOrderID.equals(orderId);
    }

    private void getOrder(String orderID) {
        new GetOrderPollingCall(remote, orderID, new Callback<Order>() {
            @Override
            public void onResponse(final Order order) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        externalOrderCallbacks
                            .onOrderConfirmed(((JWTBodyPaymentConfirmationResult) order.getResult()).getJwt());
                    }
                });

            }

            @Override
            public void onFailure(final Throwable t) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        externalOrderCallbacks.onOrderFailed(t.getMessage());
                    }
                });
            }
        }).start();
    }

    private void runOnMainThread(Runnable runnable) {
        mainThreadExecutor.execute(runnable);
    }

    interface ExternalOrderCallbacks {

        void onOrderConfirmed(String confirmationJwt);

        void onOrderFailed(String msg);
    }

    interface ExternalSpendOrderCallbacks extends ExternalOrderCallbacks {

        void onTransactionSent(OpenOrder openOrder);

        void onTransactionFailed(OpenOrder openOrder, String msg);
    }

    interface ExternalEarnOrderCallbacks extends ExternalOrderCallbacks {

        void onOrderCreated(OpenOrder openOrder);
    }
}
