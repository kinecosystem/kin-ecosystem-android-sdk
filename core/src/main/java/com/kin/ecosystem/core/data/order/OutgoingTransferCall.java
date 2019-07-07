package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;

import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.SignTransactionListener;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.blockchain.Payment;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.OutgoingTransferRequest;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import kin.sdk.migration.common.exception.OperationFailedException;

public class OutgoingTransferCall extends Thread {

    private static final int SSE_TIMEOUT = 30 * 1000; // 30 seconds
    private BlockchainSource blockchainSource = BlockchainSourceImpl.getInstance();
    private OutgoingTransferRequest request;
    private OutgoingTransferCallback callback;
    private String transferInfo;
    private final AtomicBoolean isTimeoutTaskCanceled;
    private final Timer sseTimeoutTimer;
    private Observer<Payment> paymentObserver;


    OutgoingTransferCall(@NonNull OutgoingTransferRequest request, String transferInfo, @NonNull OutgoingTransferCallback callback) {
        this.request = request;
        this.callback = callback;
        this.transferInfo = transferInfo;
        this.isTimeoutTaskCanceled = new AtomicBoolean(false);
        this.sseTimeoutTimer = new Timer();
    }

    @Override
    public void run() {
        try {
            OrderRepository orderRepository = OrderRepository.getInstance();
            final OpenOrder order = orderRepository.createOutgoingTransferOrderSync(request);
            payForOrder(orderRepository, order);
        } catch (ApiException e) {
            callback.onOutgoingTransferFailed(request, e.getMessage());
        } catch (Exception e) {
            callback.onOutgoingTransferFailed(request, e.getMessage());
        }
    }

    private void payForOrder(final OrderRepository orderRepository, final OpenOrder order) {
        try {
            blockchainSource.signTransaction(request.getWalletAddress(), new BigDecimal(request.getAmount()), request.getMemo(), order.getOfferId(), new SignTransactionListener() {
                @Override
                public void onTransactionSigned(@NonNull String transaction) {

                    listenToPayment(order, transaction);

                    orderRepository.submitSpendOrder(order.getOfferId(), transaction, order.getId(), transferInfo,
                            new KinCallback<Order>() {
                                @Override
                                public void onResponse(Order order) {
                                    scheduleTimeoutTimer();
                                }

                                @Override
                                public void onFailure(KinEcosystemException exception) {
                                    callback.onOutgoingTransferFailed(request, exception.getMessage());
                                }
                            });
                }
            });
        } catch (OperationFailedException e) {
            callback.onOutgoingTransferFailed(request, e.getMessage());
        }
    }

    private void listenToPayment(final OpenOrder order, @NonNull String transaction) {
        createPaymentObserver(order, blockchainSource.extractTransactionId(transaction));
        blockchainSource.addPaymentObservable(paymentObserver);
    }


    private void createPaymentObserver(final OpenOrder order, final String transactionId) {
        paymentObserver = new Observer<Payment>() {
            @Override
            public void onChanged(final Payment payment) {
                if (isTransferPayment(payment)) {
                    blockchainSource.removePaymentObserver(this);
                    //Cancel SSE timeout task
                    if (!isTimeoutTaskCanceled.getAndSet(true)) {
                        sseTimeoutTimer.cancel();
                    }

                    if (payment.isSucceed()) {
                        callback.onOutgoingTransferSuccess(request, order, transactionId);
                    } else {
                        callback.onOutgoingTransferFailed(request, payment.getException().getMessage());
                    }

                }
            }
        };
    }

    private void scheduleTimeoutTimer() {
        sseTimeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TimerTask runs on a BG thread of the timer.
                if (!isTimeoutTaskCanceled.getAndSet(true)) {
                    // Timeout should be fulfilled, remove payment observer and start server polling for order.
                    blockchainSource.removePaymentObserver(paymentObserver);
                    sseTimeoutTimer.cancel(); // Clear queue so it can be GC
                }
            }
        }, SSE_TIMEOUT);
    }

    private boolean isTransferPayment(Payment payment) {
        return request.getMemo().equals(payment.getOrderID());
    }


    interface OutgoingTransferCallback {

        void onOutgoingTransferSuccess(OutgoingTransferRequest request, OpenOrder order, String transactionId);

        void onOutgoingTransferFailed(OutgoingTransferRequest request, String errorMessage);
    }

}
