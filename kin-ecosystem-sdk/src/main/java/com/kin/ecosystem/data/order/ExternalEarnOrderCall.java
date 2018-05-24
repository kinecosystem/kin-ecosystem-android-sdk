package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource.Remote;
import com.kin.ecosystem.network.model.Offer.OfferType;

class ExternalEarnOrderCall extends CreateExternalOrderCall {

    ExternalEarnOrderCall(
        @NonNull Remote remote,
        @NonNull IBlockchainSource blockchainSource,
        @NonNull String orderJwt,
        @NonNull ExternalOrderCallbacks externalEarnOrderCallbacks) {
        super(remote, blockchainSource, orderJwt, externalEarnOrderCallbacks);
    }
}