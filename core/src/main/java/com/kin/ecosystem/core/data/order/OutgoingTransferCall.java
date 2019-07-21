package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;

import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.SignTransactionListener;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.blockchain.Payment;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.OutgoingTransfer;
import com.kin.ecosystem.core.util.ErrorUtil;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import kin.sdk.migration.common.exception.OperationFailedException;

public class OutgoingTransferCall extends Thread {

    private static final int SSE_TIMEOUT = 15 * 1000; // 15 seconds
    private OutgoingTransfer payload;
    private OutgoingTransferCallback callback;
    private String transferTitle;
    private final AtomicBoolean isTimeoutTaskCanceled;
    private final Timer sseTimeoutTimer;
    private Observer<Payment> paymentObserver;
    private OrderDataSource orderDataSource;
    private BlockchainSource blockchainSource;


    OutgoingTransferCall(@NonNull BlockchainSource blockchainSource, @NonNull OrderDataSource orderDataSource, @NonNull OutgoingTransfer payload, String transferTitle, @NonNull OutgoingTransferCallback callback) {
        this.payload = payload;
        this.callback = callback;
        this.transferTitle = transferTitle;
        this.isTimeoutTaskCanceled = new AtomicBoolean(false);
        this.sseTimeoutTimer = new Timer();
        this.orderDataSource = orderDataSource;
        this.blockchainSource = blockchainSource;
    }

    @Override
    public void run() {
        try {
            final OpenOrder order = orderDataSource.createOutgoingTransferOrderSync(payload);
            payForOrder(orderDataSource, order);
        } catch (KinEcosystemException exception) {
            callback.onOutgoingTransferFailed(payload, exception);
        }
    }

    private void payForOrder(final OrderDataSource orderRepository, final OpenOrder order) {
        try {
            blockchainSource.signTransaction(payload.getWalletAddress(), new BigDecimal(payload.getAmount()), payload.getMemo(), order.getOfferId(), new SignTransactionListener() {
                @Override
                public void onTransactionSigned(@NonNull String transaction) {

                    listenToPayment(order, transaction);

                    orderRepository.submitSpendOrder(order.getOfferId(), transaction, order.getId(), transferTitle,
                            new KinCallback<Order>() {
                                @Override
                                public void onResponse(Order order) {
                                    scheduleTimeoutTimer();
                                }

                                @Override
                                public void onFailure(KinEcosystemException exception) {
                                    callback.onOutgoingTransferFailed(payload, exception);
                                }
                            });
                }
            });
        } catch (OperationFailedException e) {
            callback.onOutgoingTransferFailed(payload, ErrorUtil.getBlockchainException(e));
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
                        callback.onOutgoingTransferSuccess(payload, order, transactionId);
                    } else {
                        callback.onOutgoingTransferFailed(payload, ErrorUtil.getBlockchainException(payment.getException()));
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
        return payload.getMemo().equals(payment.getOrderID());
    }


    interface OutgoingTransferCallback {

        void onOutgoingTransferSuccess(OutgoingTransfer request, OpenOrder order, String transactionId);

        void onOutgoingTransferFailed(OutgoingTransfer request, KinEcosystemException exception);
    }

}
