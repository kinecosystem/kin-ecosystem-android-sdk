package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource.Remote;

public class ExternalSpendOrderCall extends CreateExternalOrderCall {

	public ExternalSpendOrderCall(
		@NonNull Remote remote,
		@NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt,
		@NonNull EventLogger eventLogger,
		@NonNull ExternalSpendOrderCallbacks externalSpendOrderCallbacks) {
		super(remote, blockchainSource, orderJwt, eventLogger, externalSpendOrderCallbacks);
	}
}
