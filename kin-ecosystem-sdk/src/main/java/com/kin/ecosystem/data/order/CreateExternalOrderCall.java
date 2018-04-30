package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import android.util.Log;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.JWTBodyConfirmPaymentResult;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.util.ExecutorsUtil.MainThreadExecutor;
import java.math.BigDecimal;

class CreateExternalOrderCall extends Thread {

    private static final String TAG = CreateExternalOrderCall.class.getSimpleName();

    private OrderDataSource.Remote remote;
    private IBlockchainSource blockchainSource;
    private String orderJwt;
    private ExternalOrderCallbacks externalOrderCallbacks;

    private OpenOrder openOrder;
    private MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

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
        } catch (final ApiException e) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    externalOrderCallbacks.onOrderFailed(e.getResponseBody().getError());
                }
            });
            return;
        }
        // Send transaction to the network.
        blockchainSource.sendTransaction(openOrder.getBlockchainData().getRecipientAddress(),
            new BigDecimal(openOrder.getAmount()), openOrder.getId());

        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                externalOrderCallbacks.onTransactionSent(openOrder);
            }
        });

        //Listen for payments, make sure the transaction succeed.
        blockchainSource.addPaymentObservable(new Observer<Payment>() {
            @Override
            public void onChanged(final Payment payment) {
                Log.d(TAG,
                    "addPaymentObservable onChanged: " + payment.getOrderID() + " isSucceed: " + payment.isSucceed());
                if (payment.isSucceed()) {
                    getOrder(payment.getOrderID());
                } else {
                   runOnMainThread(new Runnable() {
                       @Override
                       public void run() {
                           externalOrderCallbacks.onTransactionFailed(openOrder);
                       }
                   });
                }
                blockchainSource.removePaymentObserver(this);
            }
        });
    }

    private void getOrder(String orderID) {
        new GetOrderPollingCall(remote, orderID, new Callback<Order>() {
            @Override
            public void onResponse(final Order order) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        externalOrderCallbacks
                            .onOrderConfirmed(((JWTBodyConfirmPaymentResult) order.getResult()).getJwt());
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

        void onTransactionSent(OpenOrder openOrder);

        void onTransactionFailed(OpenOrder openOrder);

        void onOrderConfirmed(String confirmationJwt);

        void onOrderFailed(String msg);

    }
}
