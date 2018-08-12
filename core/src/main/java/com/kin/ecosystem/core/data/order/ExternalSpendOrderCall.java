package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.order.OrderDataSource.Remote;

class ExternalSpendOrderCall extends CreateExternalOrderCall {

	ExternalSpendOrderCall(
		@NonNull OrderDataSource orderRepository,
		@NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt,
		@NonNull EventLogger eventLogger,
		@NonNull ExternalOrderCallbacks externalSpendOrderCallbacks) {
		super(orderRepository, blockchainSource, orderJwt, eventLogger, externalSpendOrderCallbacks);
	}
}
