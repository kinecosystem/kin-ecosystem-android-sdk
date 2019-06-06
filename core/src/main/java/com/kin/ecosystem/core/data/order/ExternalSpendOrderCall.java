package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.SpendOrderCompletionSubmitted;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationFailed;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationReceived;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.SignTransactionListener;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.StringUtil;
import java.math.BigDecimal;
import kin.sdk.migration.common.exception.OperationFailedException;

class ExternalSpendOrderCall extends CreateExternalOrderCall {

	ExternalSpendOrderCall(
		@NonNull OrderDataSource orderRepository,
		@NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt,
		@NonNull EventLogger eventLogger,
		@NonNull ExternalSpendOrderCallbacks externalSpendOrderCallbacks) {
		super(orderRepository, blockchainSource, orderJwt, eventLogger, externalSpendOrderCallbacks);
	}

	@Override
	void sendOrderCreationFailedEvent(String offerId, KinEcosystemException exception) {
		final String reason = exception.getMessage();
		final String finalOfferId = StringUtil.safeGuardNullString(offerId);
		eventLogger.send(SpendOrderCreationFailed.create(reason, finalOfferId, true, SpendOrderCreationFailed.Origin.EXTERNAL));
	}

	@Override
	void sendOrderCreationReceivedEvent(String offerId, String orderId) {
		final String finalOfferId = StringUtil.safeGuardNullString(offerId);
		final String finalOrderId = StringUtil.safeGuardNullString(orderId);
		eventLogger.send(SpendOrderCreationReceived.create(finalOfferId, finalOrderId, true, SpendOrderCreationReceived.Origin.EXTERNAL));
	}

	@Override
	void sendCompletionSubmittedEvent(String offerId, String orderId) {
		final String finalOfferId = StringUtil.safeGuardNullString(offerId);
		final String finalOrderId = StringUtil.safeGuardNullString(orderId);
		eventLogger.send(SpendOrderCompletionSubmitted.create(finalOfferId, finalOrderId, true, SpendOrderCompletionSubmitted.Origin.EXTERNAL));
	}

	@Override
	OfferType getOfferType() {
		return OfferType.SPEND;
	}

	@Override
	void sendKin2Order(final String orderId, final String offerId, final String address, final BigDecimal amount) {
		orderRepository.submitSpendOrder(offerId, null, orderId, new KinCallback<Order>() {
			@Override
			public void onResponse(Order order) {
				blockchainSource.sendTransaction(address, amount, orderId, offerId);
				onSubmissionSucceed(order.getOrderId());
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				onSubmissionFailed(offerId, orderId, exception);
			}
		});
	}

	@Override
	void sendKin3Order(final String orderId,final  String offerId, final String address, final  BigDecimal amount) {
		try {
			blockchainSource.signTransaction(address, amount, orderId, offerId, new SignTransactionListener() {
				@Override
				public void onTransactionSigned(@NonNull String transaction) {
					orderRepository.submitSpendOrder(offerId, transaction, orderId, new KinCallback<Order>() {
						@Override
						public void onResponse(Order order) {
							onSubmissionSucceed(order.getOrderId());
						}

						@Override
						public void onFailure(KinEcosystemException exception) {
							onSubmissionFailed(offerId, orderId, exception);
						}
					});
				}
			});
		} catch (OperationFailedException e) {
			final KinEcosystemException exception = new KinEcosystemException(BlockchainException.SIGN_TRANSACTION_FAILED,
				ErrorUtil.getMessage(e, "Sign Transaction Failed"), e);
			onSubmissionFailed(offerId, orderId, exception);
		}
	}
}
