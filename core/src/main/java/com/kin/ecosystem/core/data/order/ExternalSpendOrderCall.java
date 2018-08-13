package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;

class ExternalSpendOrderCall extends CreateExternalOrderCall {

	ExternalSpendOrderCall(
		@NonNull OrderDataSource orderRepository,
		@NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt,
		@NonNull EventLogger eventLogger,
		@NonNull ExternalSpendOrderCallbacks externalSpendOrderCallbacks) {
		super(orderRepository, blockchainSource, orderJwt, eventLogger, externalSpendOrderCallbacks);
	}
}
