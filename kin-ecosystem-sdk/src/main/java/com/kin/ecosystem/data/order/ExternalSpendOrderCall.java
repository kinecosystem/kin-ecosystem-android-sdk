package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource.Remote;
import com.kin.ecosystem.network.model.Offer.OfferType;

public class ExternalSpendOrderCall extends CreateExternalOrderCall {

    public ExternalSpendOrderCall(
        @NonNull Remote remote,
        @NonNull IBlockchainSource blockchainSource,
        @NonNull String orderJwt,
        @NonNull ExternalSpendOrderCallbacks externalSpendOrderCallbacks) {
        super(remote, blockchainSource, orderJwt, externalSpendOrderCallbacks);
    }
}
