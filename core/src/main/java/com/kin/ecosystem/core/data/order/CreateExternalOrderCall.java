package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.EarnOrderCompletionSubmitted;
import com.kin.ecosystem.core.bi.events.EarnOrderCreationFailed;
import com.kin.ecosystem.core.bi.events.EarnOrderCreationReceived;
import com.kin.ecosystem.core.bi.events.SpendOrderCompletionSubmitted;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationFailed;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationReceived;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
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
import kin.core.exception.InsufficientKinException;

class CreateExternalOrderCall extends Thread {

	private static final int SSE_TIMEOUT = 15000; // 15 seconds
	private final OrderDataSource orderRepository;
	private final BlockchainSource blockchainSource;
	private final String orderJwt;
	private final ExternalOrderCallbacks externalOrderCallbacks;
	private final EventLogger eventLogger;

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
	}

	@Override
	public void run() {
		try {
			// Create external order
			openOrder = orderRepository.createExternalOrderSync(orderJwt);
			sendOrderCreationReceivedEvent();
			if (isSpendOrder(openOrder)) {
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

		//Create SSE timeout timer, so we can schedule timed task.
		final Timer sseTimeoutTimer = new Timer();
		final AtomicBoolean isTimeoutTaskCanceled = new AtomicBoolean(false);

		//Listen for payments, make sure the transaction succeed.
		final Observer<Payment> paymentObserver = new Observer<Payment>() {
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
						if (isSpendOrder(openOrder)) {
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
		};
		blockchainSource.addPaymentObservable(paymentObserver);

		sendCompletionSubmittedEvent(openOrder);
		orderRepository.submitOrder(openOrder.getOfferId(), null, openOrder.getId(), new KinCallback<Order>() {
			@Override
			public void onResponse(Order response) {
				if (isSpendOrder(openOrder)) {
					// Send transaction to the blockchain
					blockchainSource.sendTransaction(openOrder.getBlockchainData().getRecipientAddress(),
						new BigDecimal(openOrder.getAmount()), openOrder.getId(), openOrder.getOfferId());
				}

				// Schedule sse timeout task
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

			@Override
			public void onFailure(KinEcosystemException e) {
				blockchainSource.removePaymentObserver(paymentObserver);
				onOrderFailed(e);
			}
		});
	}

	private boolean isSpendOrder(OpenOrder openOrder) {
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
					eventLogger.send(EarnOrderCompletionSubmitted.create(openOrder.getOfferId(), openOrder.getId(),
						EarnOrderCompletionSubmitted.Origin.EXTERNAL));
					break;
			}

		}
	}

	private void sendOrderCreationFailedEvent(final OpenOrder openOrder, ApiException exception) {
		if (openOrder != null && openOrder.getOfferType() != null) {
			final Throwable cause = exception.getCause();
			final String reason = cause != null ? cause.getMessage() : exception.getMessage();
			switch (openOrder.getOfferType()) {
				case SPEND:
					eventLogger.send(SpendOrderCreationFailed
						.create(reason, openOrder.getOfferId(), true, SpendOrderCreationFailed.Origin.EXTERNAL));
					break;
				case EARN:
					eventLogger.send(EarnOrderCreationFailed
						.create(reason, openOrder.getOfferId(), EarnOrderCreationFailed.Origin.EXTERNAL));
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
					eventLogger.send(EarnOrderCreationReceived
						.create(openOrder.getOfferId(), openOrder.getId(), EarnOrderCreationReceived.Origin.EXTERNAL));
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
