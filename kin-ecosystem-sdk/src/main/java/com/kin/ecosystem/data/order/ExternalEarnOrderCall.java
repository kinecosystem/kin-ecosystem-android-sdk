package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource.Remote;
import com.kin.ecosystem.network.model.Offer.OfferType;

public class ExternalEarnOrderCall extends CreateExternalOrderCall {

    public ExternalEarnOrderCall(
        @NonNull Remote remote,
        @NonNull IBlockchainSource blockchainSource,
        @NonNull String orderJwt,
        @NonNull ExternalEarnOrderCallbacks externalEarnOrderCallbacks) {
        super(remote, blockchainSource, orderJwt, externalEarnOrderCallbacks);
    }

    @Override
    OfferType getOfferType() {
        return OfferType.EARN;
    }
}