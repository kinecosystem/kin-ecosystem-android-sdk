package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.SpendOrderCompletionSubmitted;
import com.kin.ecosystem.bi.events.SpendOrderCreationFailed;
import com.kin.ecosystem.bi.events.SpendOrderCreationReceived;
import com.kin.ecosystem.bi.events.SpendTransactionBroadcastToBlockchainSubmitted;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.network.model.Offer.OfferType;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.util.ErrorUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import kin.ecosystem.core.network.ApiException;
import kin.ecosystem.core.util.ExecutorsUtil.MainThreadExecutor;

class CreateExternalOrderCall extends Thread {

	private final OrderDataSource.Remote remote;
	private final BlockchainSource blockchainSource;
	private final String orderJwt;
	private final ExternalOrderCallbacks externalOrderCallbacks;
	private final EventLogger eventLogger;

	private OpenOrder openOrder;
	private MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

	CreateExternalOrderCall(@NonNull OrderDataSource.Remote remote, @NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt, EventLogger eventLogger,
		@NonNull ExternalOrderCallbacks externalOrderCallbacks) {
		this.remote = remote;
		this.blockchainSource = blockchainSource;
		this.orderJwt = orderJwt;
		this.eventLogger = eventLogger;
		this.externalOrderCallbacks = externalOrderCallbacks;
	}

	@Override
	public void run() {
		try {
			// Create external order
			openOrder = remote.createExternalOrderSync(orderJwt);

			sendOrderCreationReceivedEvent();

			runOnMainThread(new Runnable() {
				@Override
				public void run() {
					externalOrderCallbacks.onOrderCreated(openOrder);
				}
			});
		} catch (final ApiException e) {
			if (isOrderConflictError(e)) {
				String orderID = extractOrderID(e.getResponseHeaders());
				getOrder(orderID);
			} else {

				onOrderFailed(ErrorUtil.fromApiException(e));
			}
			return;
		}

		if (externalOrderCallbacks instanceof ExternalSpendOrderCallbacks) {
			// Send transaction to the network.
			sendSpendOrderCompletionSubmittedEvent();
			blockchainSource.sendTransaction(openOrder.getBlockchainData().getRecipientAddress(),
				new BigDecimal(openOrder.getAmount()), openOrder.getId(), openOrder.getOfferId());

			runOnMainThread(new Runnable() {
				@Override
				public void run() {
					((ExternalSpendOrderCallbacks) externalOrderCallbacks).onTransactionSent(openOrder);
				}
			});
		}

		//Listen for payments, make sure the transaction succeed.
		blockchainSource.addPaymentObservable(new Observer<Payment>() {
			@Override
			public void onChanged(final Payment payment) {
				if (isPaymentOrderEquals(payment, openOrder.getId())) {
					if (payment.isSucceed()) {
						getOrder(payment.getOrderID());
					} else {
						if (externalOrderCallbacks instanceof ExternalSpendOrderCallbacks) {
							runOnMainThread(new Runnable() {
								@Override
								public void run() {
									((ExternalSpendOrderCallbacks) externalOrderCallbacks)
										.onTransactionFailed(openOrder,
											ErrorUtil.getBlockchainException(payment.getException()));
								}
							});
						}
					}
					blockchainSource.removePaymentObserver(this);
				}
			}
		});
	}

	private void sendOrderCreationReceivedEvent() {
		if (openOrder != null) {
			switch (openOrder.getOfferType()) {
				case SPEND:
					eventLogger.send(SpendOrderCreationReceived
						.create(openOrder.getOfferId(), openOrder.getId(), true));
					break;
				case EARN:
					break;
			}
		}
	}

	private void sendSpendOrderCompletionSubmittedEvent() {
		if (openOrder != null) {
			switch (openOrder.getOfferType()) {
				case SPEND:
					eventLogger
						.send(SpendOrderCompletionSubmitted.create(openOrder.getOfferId(), openOrder.getId(), true));

					break;
				case EARN:
					break;
			}
		}
	}

	private String extractOrderID(Map<String, List<String>> responseHeaders) {
		String orderID = null;
		List<String> locationList = responseHeaders.get("location");
		if (locationList != null && locationList.size() > 0) {
			String url = locationList.get(0);
			String[] parts = url.split("/");
			orderID = parts[parts.length - 1];
		}

		return orderID;
	}

	private boolean isOrderConflictError(ApiException e) {
		return e.getCode() == 409 && e.getResponseBody().getCode() == 4091;
	}

	private boolean isPaymentOrderEquals(Payment payment, String orderId) {
		String paymentOrderID = payment.getOrderID();
		return paymentOrderID != null && paymentOrderID.equals(orderId);
	}

	private void getOrder(String orderID) {
		new GetOrderPollingCall(remote, orderID, new Callback<Order, ApiException>() {
			@Override
			public void onResponse(final Order order) {
				runOnMainThread(new Runnable() {
					@Override
					public void run() {
						externalOrderCallbacks
							.onOrderConfirmed(((JWTBodyPaymentConfirmationResult) order.getResult()).getJwt(), order);
					}
				});

			}

			@Override
			public void onFailure(final ApiException e) {
				onOrderFailed(ErrorUtil.fromApiException(e));
			}
		}).start();
	}

	private void onOrderFailed(final KinEcosystemException exception) {
		if (openOrder != null) {
			switch (openOrder.getOfferType()) {
				case SPEND:
					eventLogger.send(SpendOrderCreationFailed
						.create(exception.getCause().getMessage(), openOrder.getOfferId(), true));
					break;
				case EARN:
					break;
			}

		}
		final OpenOrder finalOpenOrder = openOrder;
		runOnMainThread(new Runnable() {
			@Override
			public void run() {
				externalOrderCallbacks
					.onOrderFailed(
						exception, finalOpenOrder
					);
			}
		});
	}

	private void runOnMainThread(Runnable runnable) {
		mainThreadExecutor.execute(runnable);
	}

	private String getApiExceptionsMessage(Throwable t) {
		try {
			return ((ApiException) t).getResponseBody().getMessage();
		} catch (Exception e) {
			return hasMessage(t) ? t.getMessage() : "Task failed";
		}
	}

	private boolean hasMessage(Throwable t) {
		return t != null && t.getMessage() != null && !t.getMessage().isEmpty();
	}

	interface ExternalOrderCallbacks {

		void onOrderCreated(OpenOrder openOrder);

		void onOrderConfirmed(String confirmationJwt, Order order);

		void onOrderFailed(KinEcosystemException exception, OpenOrder order);
	}

	interface ExternalSpendOrderCallbacks extends ExternalOrderCallbacks {

		void onTransactionSent(OpenOrder openOrder);

		void onTransactionFailed(OpenOrder openOrder, KinEcosystemException exception);
	}
}
