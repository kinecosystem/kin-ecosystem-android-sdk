package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;

class ExternalEarnOrderCall extends CreateExternalOrderCall {

    ExternalEarnOrderCall(
        @NonNull OrderDataSource orderRepository,
        @NonNull BlockchainSource blockchainSource,
        @NonNull String orderJwt,
        @NonNull EventLogger eventLogger,
        @NonNull ExternalOrderCallbacks externalEarnOrderCallbacks) {
        super(orderRepository, blockchainSource, orderJwt, eventLogger, externalEarnOrderCallbacks);
    }
}