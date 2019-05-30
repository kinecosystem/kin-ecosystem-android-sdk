package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.EarnOrderCompletionSubmitted;
import com.kin.ecosystem.core.bi.events.EarnOrderCreationFailed;
import com.kin.ecosystem.core.bi.events.EarnOrderCreationReceived;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.Order;
import java.math.BigDecimal;

class ExternalEarnOrderCall extends CreateExternalOrderCall {

	ExternalEarnOrderCall(
		@NonNull OrderDataSource orderRepository,
		@NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt,
		@NonNull EventLogger eventLogger,
		@NonNull ExternalOrderCallbacks externalEarnOrderCallbacks) {
		super(orderRepository, blockchainSource, orderJwt, eventLogger, externalEarnOrderCallbacks);
	}

	@Override
	void sendOrderCreationFailedEvent(String offerId, KinEcosystemException exception) {
		final String reason = exception.getMessage();
		eventLogger.send(EarnOrderCreationFailed.create(reason, offerId, EarnOrderCreationFailed.Origin.EXTERNAL));
	}

	@Override
	void sendOrderCreationReceivedEvent(String offerId, String orderId) {
		eventLogger.send(EarnOrderCreationReceived.create(offerId, orderId, EarnOrderCreationReceived.Origin.EXTERNAL));
	}

	@Override
	void sendCompletionSubmittedEvent(String offerId, String orderId) {
		eventLogger.send(EarnOrderCompletionSubmitted.create(offerId, orderId, EarnOrderCompletionSubmitted.Origin.EXTERNAL));
	}

	@Override
	OfferType getOfferType() {
		return OfferType.EARN;
	}

	@Override
	void sendKin2Order(final String orderId, final String offerId, final String address, final BigDecimal amount) {
		submitOrder(offerId, orderId);
	}

	@Override
	void sendKin3Order(String orderId, String offerId, String address, BigDecimal amount) {
		submitOrder(offerId, orderId);
	}

	private void submitOrder(final String offerId, final String orderId) {
		orderRepository.submitEarnOrder(offerId, null, orderId, new KinCallback<Order>() {
			@Override
			public void onResponse(Order order) {
				onSubmissionSucceed(order.getOrderId());
			}

			@Override
			public void onFailure(KinEcosystemException e) {
				onSubmissionFailed(offerId, orderId, e);
			}
		});
	}
}