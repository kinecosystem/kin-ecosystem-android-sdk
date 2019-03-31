package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.SpendOrderCompletionSubmitted;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationFailed;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationReceived;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.SignTransactionListener;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceLocal;
import com.kin.ecosystem.core.data.blockchain.Payment;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.ExecutorsUtil.MainThreadExecutor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import kin.sdk.migration.common.KinSdkVersion;
import kin.sdk.migration.common.exception.InsufficientKinException;
import kin.sdk.migration.common.exception.OperationFailedException;

class CreateExternalOrderCall extends Thread {
	private static final int SSE_TIMEOUT = 15000; // 15 seconds
	private final OrderDataSource orderRepository;
	private final BlockchainSource blockchainSource;
	private final String orderJwt;
	private final ExternalOrderCallbacks externalOrderCallbacks;
	private final EventLogger eventLogger;
	private final Timer sseTimeoutTimer;
	private final AtomicBoolean isTimeoutTaskCanceled;
	private final Observer<Payment> paymentObserver;

	private OpenOrder openOrder;
	private MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

	CreateExternalOrderCall(@NonNull OrderDataSource orderRepository, @NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt, @NonNull EventLogger eventLogger,
		@NonNull ExternalOrderCallbacks externalOrderCallbacks) {
		this.orderRepository = orderRepository;
		this.blockchainSource = blockchainSource;
		this.orderJwt = orderJwt;
		this.eventLogger = eventLogger;
		this.externalOrderCallbacks = externalOrderCallbacks;
		this.sseTimeoutTimer = new Timer();
		this.isTimeoutTaskCanceled = new AtomicBoolean(false);

		this.paymentObserver = new Observer<Payment>() {
			@Override
			public void onChanged(final Payment payment) {
				if (isPaymentOrderEquals(payment, openOrder.getId())) {
					//Cancel SSE timeout task
					if (!isTimeoutTaskCanceled.getAndSet(true)) {
						sseTimeoutTimer.cancel();
					}

					if (payment.isSucceed()) {
						getOrder(payment.getOrderID());
					} else {
						if (isSpendOrder()) {
							runOnMainThread(new Runnable() {
								@Override
								public void run() {
									((ExternalSpendOrderCallbacks) CreateExternalOrderCall.this.externalOrderCallbacks)
										.onTransactionFailed(openOrder,
											ErrorUtil.getBlockchainException(payment.getException()));
								}
							});
						}
					}

					CreateExternalOrderCall.this.blockchainSource.removePaymentObserver(this);
				}
			}
		};
	}

	@Override
	public void run() {
		try {
			// Create external order
			openOrder = orderRepository.createExternalOrderSync(orderJwt);
			sendOrderCreationReceivedEvent();
			if (isSpendOrder()) {
				Balance balance = blockchainSource.getBalance();
				if (balance.getAmount().intValue() < openOrder.getAmount()) {
					orderRepository.cancelOrderSync(openOrder.getId());
					runOnMainThread(new Runnable() {
						@Override
						public void run() {
							externalOrderCallbacks
								.onOrderFailed(ErrorUtil.getBlockchainException(new InsufficientKinException()),
									openOrder);
						}
					});
					return;
				}
			}
		} catch (final ApiException e) {
			if (isOrderConflictError(e)) {
				String orderID = extractOrderID(e.getResponseHeaders());
				getOrder(orderID);
			} else {
				sendOrderCreationFailedEvent(openOrder, e);
				onOrderFailed(ErrorUtil.fromApiException(e));
			}
			return;
		}

		final String orderId = openOrder.getId();
		final String offerId = openOrder.getOfferId();
		final BigDecimal amount = new BigDecimal(openOrder.getAmount());
		final String address = openOrder.getBlockchainData().getRecipientAddress();

		blockchainSource.addPaymentObservable(paymentObserver);
		if (blockchainSource.getBlockchainVersion() == KinSdkVersion.NEW_KIN_SDK) {
			sendKin3Order(orderId, offerId, address, amount);
		} else {
			sendKin2Order(orderId, offerId, address, amount);
		}

		sendCompletionSubmittedEvent(openOrder);
	}

	private void sendKin2Order(final String orderId, final String offerId, final String address, final BigDecimal amount) {
		KinCallback<Order> callback = new KinCallback<Order>() {
			@Override
			public void onResponse(Order response) {
				if (isSpendOrder()) {
					// Send transaction to the blockchain
					blockchainSource.sendTransaction(address, amount, orderId, offerId);
				}

				scheduleTimer();
			}

			@Override
			public void onFailure(KinEcosystemException e) {
				blockchainSource.removePaymentObserver(paymentObserver);
				onOrderFailed(e);
			}
		};

		if (isSpendOrder()) {
			orderRepository.submitSpendOrder(offerId, null, orderId, callback);
		} else {
			orderRepository.submitEarnOrder(offerId, null, orderId, callback);
		}
	}

	private void sendKin3Order(final String orderId, final String offerId, final String address, final BigDecimal amount) {
		Logger.log(new Log().withTag("MOO").text("sendKin3Order 1"));

		final KinCallback<Order> callback = new KinCallback<Order>() {
			@Override
			public void onResponse(Order response) {
				Logger.log(new Log().withTag("MOO").text("sendKin3Order 2"));
				if (isSpendOrder()) {
					// Send transaction to the blockchain
					// blockchainSource.sendTransaction(address, amount, orderId, offerId);
				}

				scheduleTimer();
			}

			@Override
			public void onFailure(KinEcosystemException e) {
				Logger.log(new Log().withTag("MOO").text("sendKin3Order 3"));
				blockchainSource.removePaymentObserver(paymentObserver);
				onOrderFailed(e);
			}
		};

		if (isSpendOrder()) {
			Logger.log(new Log().withTag("MOO").text("sendKin3Order 4"));
			try {
				blockchainSource.signTransaction(address, amount, orderId, offerId, new SignTransactionListener() {
					@Override
					public void onTransactionSigned(@NonNull String transaction) {
						Logger.log(new Log().withTag("MOO").text("sendKin3Order 5"));
						orderRepository.submitSpendOrder(offerId, transaction, orderId, callback);
					}
				});
			} catch (OperationFailedException e) {
				blockchainSource.removePaymentObserver(paymentObserver);
				onOrderFailed(new KinEcosystemException(KinEcosystemException.UNKNOWN, e.getMessage(), e));
			}
		} else {
			orderRepository.submitEarnOrder(offerId, null, orderId, callback);
		}
	}

	private void scheduleTimer() {
		sseTimeoutTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TimerTask runs on a BG thread of the timer.
				if (!isTimeoutTaskCanceled.getAndSet(true)) {
					// Timeout should be fulfilled, remove payment observer and start server polling for order.
					blockchainSource.removePaymentObserver(paymentObserver);
					getOrder(openOrder.getId());
					sseTimeoutTimer.cancel(); // Clear queue so it can be GC
				}
			}
		}, SSE_TIMEOUT);
	}

	private boolean isSpendOrder() {
		return openOrder.getOfferType() == OfferType.SPEND;
	}

	private void sendCompletionSubmittedEvent(OpenOrder openOrder) {
		if (openOrder != null && openOrder.getOfferType() != null) {
			switch (openOrder.getOfferType()) {
				case SPEND:
					eventLogger
						.send(SpendOrderCompletionSubmitted.create(openOrder.getOfferId(), openOrder.getId(), true,
							SpendOrderCompletionSubmitted.Origin.EXTERNAL));
					break;
				case EARN:
					//TODO add event
					// We don't have event currently
					break;
			}

		}
	}

	private void sendOrderCreationFailedEvent(final OpenOrder openOrder, ApiException exception) {
		if (openOrder != null && openOrder.getOfferType() != null) {
			switch (openOrder.getOfferType()) {
				case SPEND:
					final Throwable cause = exception.getCause();
					final String reason = cause != null ? cause.getMessage() : exception.getMessage();
					eventLogger.send(SpendOrderCreationFailed
						.create(reason, openOrder.getOfferId(), true, SpendOrderCreationFailed.Origin.EXTERNAL));
					break;
				case EARN:
					//TODO add event
					// We don't have event currently
					break;
			}

		}
	}

	private void sendOrderCreationReceivedEvent() {
		if (openOrder != null && openOrder.getOfferType() != null) {
			switch (openOrder.getOfferType()) {
				case SPEND:
					eventLogger.send(SpendOrderCreationReceived
						.create(openOrder.getOfferId(), openOrder.getId(), true,
							SpendOrderCreationReceived.Origin.EXTERNAL));
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
		return e.getCode() == ErrorUtil.ERROR_CODE_CONFLICT
			&& e.getResponseBody().getCode() == ErrorUtil.ERROR_CODE_EXTERNAL_ORDER_ALREADY_COMPLETED;
	}

	private boolean isPaymentOrderEquals(Payment payment, String orderId) {
		String paymentOrderID = payment.getOrderID();
		return paymentOrderID != null && paymentOrderID.equals(orderId);
	}

	private void getOrder(String orderID) {
		orderRepository.getOrder(orderID, new KinCallback<Order>() {
			@Override
			public void onResponse(final Order order) {
				switch (order.getStatus()) {
					case COMPLETED:
						runOnMainThread(new Runnable() {
							@Override
							public void run() {
								externalOrderCallbacks
									.onOrderConfirmed(((JWTBodyPaymentConfirmationResult) order.getResult()).getJwt(),
										order);
							}
						});
						break;
					case FAILED:
						String errorMessage = "External Order Failed";
						if (order.getError() != null) {
							errorMessage = order.getError().getMessage();
						}
						ServiceException serviceException = new ServiceException(ServiceException.ORDER_FAILED,
							errorMessage, null);
						onFailure(serviceException);
						break;
					case DELAYED:
						// if delayed there will be additional retries will eventually either call onResponse or onFailure
						break;
				}
			}

			@Override
			public void onFailure(KinEcosystemException e) {
				onOrderFailed(e);
			}
		});
	}

	private void onOrderFailed(final KinEcosystemException exception) {
		final OpenOrder finalOpenOrder = openOrder;
		runOnMainThread(new Runnable() {
			@Override
			public void run() {
				externalOrderCallbacks
					.onOrderFailed(exception, finalOpenOrder);
			}
		});
	}

	private void runOnMainThread(Runnable runnable) {
		mainThreadExecutor.execute(runnable);
	}

	interface ExternalOrderCallbacks {

		void onOrderConfirmed(String confirmationJwt, Order order);

		void onOrderFailed(KinEcosystemException exception, OpenOrder order);
	}

	interface ExternalSpendOrderCallbacks extends ExternalOrderCallbacks {

		void onTransactionFailed(OpenOrder openOrder, KinEcosystemException exception);
	}
}
