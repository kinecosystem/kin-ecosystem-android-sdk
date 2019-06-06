package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.GeneralEcosystemSdkError;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.Payment;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.ExecutorsUtil.MainThreadExecutor;
import com.kin.ecosystem.core.util.JwtDecoder;
import com.kin.ecosystem.core.util.StringUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import kin.sdk.migration.common.KinSdkVersion;
import kin.sdk.migration.common.exception.InsufficientKinException;
import org.json.JSONException;

abstract class CreateExternalOrderCall extends Thread {

	private static final int SSE_TIMEOUT = 15000; // 15 seconds
	protected final OrderDataSource orderRepository;
	protected final BlockchainSource blockchainSource;
	private final String orderJwt;
	private final ExternalOrderCallbacks externalOrderCallbacks;
	protected final EventLogger eventLogger;
	private final Timer sseTimeoutTimer;
	private final AtomicBoolean isTimeoutTaskCanceled;
	private Observer<Payment> paymentObserver;


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
	}

	abstract void sendOrderCreationFailedEvent(final String offerId, final KinEcosystemException exception);

	abstract void sendOrderCreationReceivedEvent(final String offerId, final String orderId);

	abstract void sendCompletionSubmittedEvent(final String offerId, final String orderId);

	abstract OfferType getOfferType();

	@Override
	public void run() {
		final OpenOrder openOrder;
		try {
			// Create external order
			 openOrder = orderRepository.createExternalOrderSync(orderJwt);
			sendOrderCreationReceivedEvent(openOrder.getOfferId(), openOrder.getId());
			if (isSpendOrder()) {
				Balance balance = blockchainSource.getBalance();
				if (balance.getAmount().intValue() < openOrder.getAmount()) {
					orderRepository.cancelOrderSync(openOrder.getId());
					runOnMainThread(new Runnable() {
						@Override
						public void run() {
							externalOrderCallbacks
								.onOrderFailed(openOrder.getOfferId(), openOrder.getId(),
									ErrorUtil.getBlockchainException(new InsufficientKinException()));
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
				OfferJwtBody offerJwtBody = null;
				try {
					offerJwtBody = JwtDecoder.getOfferJwtBody(orderJwt);
				} catch (JSONException e1) {
					eventLogger.send(GeneralEcosystemSdkError
						.create("Could not parse OfferJwtBody from order jwt. " + e1.getMessage()));
				}
				final KinEcosystemException kinEcosystemException = ErrorUtil.fromApiException(e);
				final String offerId = offerJwtBody != null ? offerJwtBody.getOfferId() : null;
				sendOrderCreationFailedEvent(offerId, kinEcosystemException);
				onOrderFailed(offerId, null, kinEcosystemException);
			}
			return;
		}

		final String orderId = openOrder.getId();
		final String offerId = openOrder.getOfferId();
		final BigDecimal amount = new BigDecimal(openOrder.getAmount());
		final String address = openOrder.getBlockchainData().getRecipientAddress();

		createPaymentObserver(offerId, orderId);
		blockchainSource.addPaymentObservable(paymentObserver);
		if (blockchainSource.getBlockchainVersion() == KinSdkVersion.NEW_KIN_SDK) {
			sendKin3Order(orderId, offerId, address, amount);
		} else {
			sendKin2Order(orderId, offerId, address, amount);
		}

		sendCompletionSubmittedEvent(offerId, orderId);
	}

	private void createPaymentObserver(final String offerId, final String orderId) {
		this.paymentObserver = new Observer<Payment>() {
			@Override
			public void onChanged(final Payment payment) {
				if (isPaymentOrderEquals(payment, orderId)) {
					blockchainSource.removePaymentObserver(this);
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
										.onTransactionFailed(offerId, orderId, ErrorUtil.getBlockchainException(payment.getException()));
								}
							});
						}
					}
				}
			}
		};
	}

	abstract void sendKin2Order(final String orderId, final String offerId, final String address, final BigDecimal amount);

	void onSubmissionSucceed(final String orderId) {
		scheduleTimeoutTimer(orderId);
	}

	void onSubmissionFailed(final String offerId, final String orderId, KinEcosystemException e) {
		blockchainSource.removePaymentObserver(paymentObserver);
		onOrderFailed(offerId, orderId, e);
	}

	abstract void sendKin3Order(final String orderId, final String offerId, final String address, final BigDecimal amount);

	private void scheduleTimeoutTimer(final String orderId) {
		sseTimeoutTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TimerTask runs on a BG thread of the timer.
				if (!isTimeoutTaskCanceled.getAndSet(true)) {
					// Timeout should be fulfilled, remove payment observer and start server polling for order.
					blockchainSource.removePaymentObserver(paymentObserver);
					getOrder(orderId);
					sseTimeoutTimer.cancel(); // Clear queue so it can be GC
				}
			}
		}, SSE_TIMEOUT);
	}

	private boolean isSpendOrder() {
		return getOfferType() == OfferType.SPEND;
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

	private void getOrder(final String orderID) {
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
						if (order.getError() != null && !StringUtil.isEmpty(order.getError().getMessage())) {
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
				OfferJwtBody offerJwtBody = null;
				try {
					offerJwtBody = JwtDecoder.getOfferJwtBody(orderJwt);
				} catch (JSONException e1) {
					eventLogger.send(GeneralEcosystemSdkError
						.create("Could not parse OfferJwtBody from order jwt on getOrder call. " + e1.getMessage()));
				}
				final String offerId = offerJwtBody != null ? offerJwtBody.getOfferId() : null;
				onOrderFailed(offerId, orderID, e);
			}
		});
	}

	private void onOrderFailed(final String offerId, final String orderId, final KinEcosystemException exception) {
		runOnMainThread(new Runnable() {
			@Override
			public void run() {
				externalOrderCallbacks.onOrderFailed(offerId, orderId, exception);
			}
		});
	}

	private void runOnMainThread(Runnable runnable) {
		mainThreadExecutor.execute(runnable);
	}

	interface ExternalOrderCallbacks {

		void onOrderConfirmed(String confirmationJwt, Order order);

		void onOrderFailed(final String offerId, final String orderId, KinEcosystemException exception);
	}

	interface ExternalSpendOrderCallbacks extends ExternalOrderCallbacks {

		void onTransactionFailed(final String offerId, final String orderId, KinEcosystemException exception);
	}
}
