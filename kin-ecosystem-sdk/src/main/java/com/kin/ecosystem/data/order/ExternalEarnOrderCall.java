package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource.Remote;

class ExternalEarnOrderCall extends CreateExternalOrderCall {

    ExternalEarnOrderCall(
        @NonNull Remote remote,
        @NonNull BlockchainSource blockchainSource,
        @NonNull String orderJwt,
        @NonNull ExternalOrderCallbacks externalEarnOrderCallbacks) {
        super(remote, blockchainSource, orderJwt, externalEarnOrderCallbacks);
    }
}