package kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import kin.ecosystem.core.bi.EventLogger;
import kin.ecosystem.core.data.blockchain.BlockchainSource;
import kin.ecosystem.core.data.order.OrderDataSource.Remote;

class ExternalSpendOrderCall extends CreateExternalOrderCall {

	ExternalSpendOrderCall(
		@NonNull Remote remote,
		@NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt,
		@NonNull EventLogger eventLogger,
		@NonNull ExternalSpendOrderCallbacks externalSpendOrderCallbacks) {
		super(remote, blockchainSource, orderJwt, eventLogger, externalSpendOrderCallbacks);
	}
}
