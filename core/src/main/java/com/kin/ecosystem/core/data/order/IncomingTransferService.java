package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;

import org.kinecosystem.transfer.receiver.service.ReceiveKinServiceBase;

public class IncomingTransferService extends ReceiveKinServiceBase {

    @Override
    protected void onTransactionCompleted(@NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo) {
    }

    @Override
    protected void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String memo) {

    }
}
