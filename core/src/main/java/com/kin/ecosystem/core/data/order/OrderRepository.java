package com.kin.ecosystem.core.data.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.DataNotAvailableException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.model.OrderConfirmation;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.EarnOrderCompleted;
import com.kin.ecosystem.core.bi.events.EarnOrderCreationRequested;
import com.kin.ecosystem.core.bi.events.EarnOrderFailed;
import com.kin.ecosystem.core.bi.events.EarnOrderPaymentConfirmed;
import com.kin.ecosystem.core.bi.events.SpendOrderCompleted;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationRequested;
import com.kin.ecosystem.core.bi.events.SpendOrderFailed;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.Payment;
import com.kin.ecosystem.core.data.order.CreateExternalOrderCall.ExternalOrderCallbacks;
import com.kin.ecosystem.core.data.order.CreateExternalOrderCall.ExternalSpendOrderCallbacks;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.Body;
import com.kin.ecosystem.core.network.model.Error;
import com.kin.ecosystem.core.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.Order.Origin;
import com.kin.ecosystem.core.network.model.Order.Status;
import com.kin.ecosystem.core.network.model.OrderList;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.StringUtil;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderRepository implements OrderDataSource {

	private static final String TAG = OrderRepository.class.getSimpleName();

	private static volatile OrderRepository instance = null;
	private final OrderDataSource.Local localData;
	private final OrderDataSource.Remote remoteData;

	private final BlockchainSource blockchainSource;
	private final EventLogger eventLogger;

	@Nullable
	private OrderList cachedOrderList;
	private ObservableData<OpenOrder> cachedOpenOrder = ObservableData.create();
	private ObservableData<Order> orderWatcher = ObservableData.create();
	private Observer<Payment> paymentObserver;

	private volatile AtomicInteger pendingOrdersCount = new AtomicInteger(0);

	private final Object paymentObserversLock = new Object();
	private int paymentObserverCount;

	private OrderRepository(@NonNull final BlockchainSource blockchainSource,
		@NonNull final EventLogger eventLogger,
		@NonNull final OrderDataSource.Remote remoteData,
		@NonNull final OrderDataSource.Local localData) {
		this.remoteData = remoteData;
		this.localData = localData;
		this.blockchainSource = blockchainSource;
		this.eventLogger = eventLogger;
	}

	public static void init(@NonNull final BlockchainSource blockchainSource,
		@NonNull final EventLogger eventLogger,
		@NonNull final OrderDataSource.Remote remoteData,
		@NonNull final OrderDataSource.Local localData) {
		if (instance == null) {
			synchronized (OrderRepository.class) {
				if (instance == null) {
					instance = new OrderRepository(blockchainSource, eventLogger, remoteData,
						localData);
				}
			}
		}
	}

	public static OrderRepository getInstance() {
		return instance;
	}

	@Nullable
	public OrderList getAllCachedOrderHistory() {
		return cachedOrderList;
	}

	@Override
	public void getAllOrderHistory(@NonNull final KinCallback<OrderList> callback) {
		remoteData.getAllOrderHistory(new Callback<OrderList, ApiException>() {
			@Override
			public void onResponse(OrderList response) {
				cachedOrderList = response;
				callback.onResponse(response);
			}

			@Override
			public void onFailure(ApiException e) {
				callback.onFailure(ErrorUtil.fromApiException(e));
			}
		});
	}

	public ObservableData<OpenOrder> getOpenOrder() {
		return cachedOpenOrder;
	}

	@Override
	public void getOrder(@NonNull String orderID, @Nullable final KinCallback<Order> callback) {
		remoteData.getOrder(orderID, new Callback<Order, ApiException>() {
			@Override
			public void onResponse(Order order) {
				decrementPendingOrdersCount();
				getOrderWatcher().postValue(order);
				sendSpendOrderCompleted(order);
				if (!hasMorePendingOffers()) {
					removeCachedOpenOrderByID(order.getOrderId());
				}
				if (callback != null) {
					callback.onResponse(order);
				}
			}

			@Override
			public void onFailure(ApiException e) {
				decrementPendingOrdersCount();
				if (callback != null) {
					callback.onFailure(ErrorUtil.fromApiException(e));
				}
			}
		});
	}

	@Override
	public void createOrder(@NonNull final String offerID, @Nullable final KinCallback<OpenOrder> callback) {
		remoteData.createOrder(offerID, new Callback<OpenOrder, ApiException>() {
			@Override
			public void onResponse(OpenOrder response) {
				cachedOpenOrder.postValue(response);
				if (callback != null) {
					callback.onResponse(response);
				}
			}

			@Override
			public void onFailure(ApiException e) {
				if (callback != null) {
					callback.onFailure(ErrorUtil.fromApiException(e));
				}
			}
		});
	}

	@Override
	public void submitEarnOrder(@NonNull final String offerID, @Nullable String content, @NonNull final String orderID, final String title,
		@Nullable final KinCallback<Order> callback) {
		listenForCompletedPayment();
		remoteData.submitEarnOrder(content, orderID, createSubmitOrderCallback(callback, orderID, offerID, title, OfferType.EARN));
	}

	@Override
	public void submitSpendOrder(@NonNull final String offerID, @Nullable String transaction,
		@NonNull final String orderID, final String title,
		@Nullable final KinCallback<Order> callback) {
		listenForCompletedPayment();
		remoteData.submitSpendOrder(transaction, orderID, createSubmitOrderCallback(callback, orderID, offerID, title, OfferType.SPEND));
	}

	@Override
	public void cancelOrderSync(@NonNull String orderID) {
		removeCachedOpenOrderByID(orderID);
		remoteData.cancelOrderSync(orderID);
	}

	private void listenForCompletedPayment() {
		synchronized (paymentObserversLock) {
			if (paymentObserverCount == 0) {
				paymentObserver = new Observer<Payment>() {
					@Override
					public void onChanged(Payment payment) {
						if (!payment.isSucceed()) {
							BlockchainException blockchainException = ErrorUtil.getBlockchainException(payment.getException());
							final Error error = new Error("Transaction failed", blockchainException.getMessage(),
								blockchainException.getCode());
							final Body body = new Body().error(error);
							changeOrder(payment.getOrderID(), body);
						}

						sendEarnPaymentConfirmed(payment);
						decrementPaymentObserverCount();
						getOrder(payment.getOrderID(), null);
					}
				};
				blockchainSource.addPaymentObservable(paymentObserver);
				Logger.log(new Log().withTag(TAG).text("listenForCompletedPayment: addPaymentObservable"));
			}
			paymentObserverCount++;
		}
	}

	private void sendEarnPaymentConfirmed(Payment payment) {
		if (payment.isSucceed() && payment.getAmount() != null && payment.getType() == Payment.EARN) {
			eventLogger.send(EarnOrderPaymentConfirmed.create(payment.getTransactionID(), payment.getOrderID()));
		}
	}

	private void decrementPaymentObserverCount() {
		synchronized (paymentObserversLock) {
			if (paymentObserverCount > 0) {
				paymentObserverCount--;
			}

			if (paymentObserverCount == 0 && paymentObserver != null) {
				blockchainSource.removePaymentObserver(paymentObserver);
				paymentObserver = null;
			}
		}
	}

	private void decrementPendingOrdersCount() {
		if (hasMorePendingOffers()) {
			pendingOrdersCount.decrementAndGet();
		}
	}

	private Callback<Order, ApiException> createSubmitOrderCallback(final KinCallback<Order> callback,
		final String orderID, final String offerID, final String title, final OfferType offerType) {
		return new Callback<Order, ApiException>() {
			@Override
			public void onResponse(Order response) {
				pendingOrdersCount.incrementAndGet();
				getOrderWatcher().postValue(response);
				if (callback != null) {
					callback.onResponse(response);
				}
			}

			@Override
			public void onFailure(ApiException e) {
				getOrderWatcher().postValue(new Order().title(title).offerType(offerType).orderId(orderID).offerId(offerID).status(Status.FAILED));
				removeCachedOpenOrderByID(orderID);
				if (callback != null) {
					callback.onFailure(ErrorUtil.fromApiException(e));
				}
			}
		};
	}

	@VisibleForTesting
	ObservableData<Order> getOrderWatcher() {
		return orderWatcher;
	}

	private void sendSpendOrderCompleted(Order order) {
		if (order.getOfferType() == OfferType.SPEND && order.getOrigin() == Origin.MARKETPLACE) {
			switch (order.getStatus()) {
				case PENDING:
					break;
				case COMPLETED:
					double amount = (double) order.getAmount();
					eventLogger.send(SpendOrderCompleted.create(order.getOfferId(), order.getOrderId(), false,
						SpendOrderCompleted.Origin.MARKETPLACE, amount));
					break;
				case DELAYED:
					break;
				case FAILED:
					String reason = "Timed out";
					if (order.getError() != null && !StringUtil.isEmpty(order.getError().getMessage())) {
						reason = order.getError().getMessage();
					}
					eventLogger.send(SpendOrderFailed.create(reason, order.getOfferId(), order.getOrderId(), false,
						SpendOrderFailed.Origin.MARKETPLACE));
					break;
			}
		}
	}

	private boolean hasMorePendingOffers() {
		return pendingOrdersCount.get() > 0;
	}

	private void removeCachedOpenOrderByID(String orderId) {
		if (isCachedOpenOrderEquals(orderId)) {
			cachedOpenOrder.postValue(null);
		}
	}

	private boolean isCachedOpenOrderEquals(String orderId) {
		if (cachedOpenOrder != null) {
			final OpenOrder openOrder = cachedOpenOrder.getValue();
			if (openOrder != null) {
				return openOrder.getId().equals(orderId);
			}
		}
		return false;
	}

	@Override
	public void cancelOrder(@NonNull final String offerID, @NonNull final String orderID,
		@Nullable final KinCallback<Void> callback) {
		removeCachedOpenOrderByID(orderID);
		remoteData.cancelOrder(orderID, new Callback<Void, ApiException>() {
			@Override
			public void onResponse(Void response) {
				if (callback != null) {
					callback.onResponse(response);
				}
			}

			@Override
			public void onFailure(ApiException e) {
				if (callback != null) {
					callback.onFailure(ErrorUtil.fromApiException(e));
				}
			}
		});
	}

	@Override
	public OpenOrder createExternalOrderSync(@NonNull final String orderJwt) throws ApiException {
		OpenOrder openOrder = remoteData.createExternalOrderSync(orderJwt);
		cachedOpenOrder.postValue(openOrder);
		return openOrder;
	}

	@Override
	public void purchase(String offerJwt, @Nullable final KinCallback<OrderConfirmation> callback) {
		eventLogger.send(SpendOrderCreationRequested.create("", true, SpendOrderCreationRequested.Origin.EXTERNAL));
		new ExternalSpendOrderCall(this, blockchainSource, offerJwt, eventLogger,
			new ExternalSpendOrderCallbacks() {

				@Override
				public void onTransactionFailed(final String offerId, final String orderId, final KinEcosystemException exception) {
					removeCachedOpenOrderByID(orderId);
					handleOnFailure(offerId, orderId, exception);
				}

				@Override
				public void onOrderConfirmed(String confirmationJwt, Order order) {
					String offerID = "null";
					String orderId = "null";
					double amount = -1;
					if (order != null) {
						offerID = order.getOfferId();
						orderId = order.getOrderId();
						amount = (double) order.getAmount();
					}
					eventLogger.send(SpendOrderCompleted
						.create(offerID, orderId, true, SpendOrderCompleted.Origin.EXTERNAL, amount));

					if (callback != null) {
						callback.onResponse(createOrderConfirmation(confirmationJwt));
					}
				}

				@Override
				public void onOrderFailed(final String offerId, final String orderId, KinEcosystemException exception) {
					if (!StringUtil.isEmpty(orderId)) { // did not fail before submit
						decrementCount();
					}
					handleOnFailure(offerId, orderId, exception);
				}

				private void handleOnFailure(final String offerId, final String orderId, KinEcosystemException exception) {
					final String finalOfferId = StringUtil.safeGuardNullString(offerId);
					final String finalOrderId = StringUtil.safeGuardNullString(orderId);
					eventLogger.send(SpendOrderFailed.create(exception.getMessage(), finalOfferId, finalOrderId, true, SpendOrderFailed.Origin.EXTERNAL));

					if (callback != null) {
						callback.onFailure(exception);
					}
				}

			}).start();
	}

	/**
	 * Update server with the relevant Body when an error occurred
	 * or something that the server should know about happened.
	 *
	 * @param orderID the Order id that you refer to
	 * @param body content with the relevant {@link Error}
	 */
	private void changeOrder(@NonNull String orderID, @NonNull Body body) {
		remoteData.changeOrder(orderID, body, new Callback<Order, ApiException>() {
			@Override
			public void onResponse(Order response) {
				// no-op
			}

			@Override
			public void onFailure(ApiException error) {
				// no-op
			}
		});
	}

	@Override
	public void requestPayment(String offerJwt, final KinCallback<OrderConfirmation> callback) {
		eventLogger.send(EarnOrderCreationRequested.create(EarnOrderCreationRequested.OfferType.EXTERNAL, null, "null",
			EarnOrderCreationRequested.Origin.EXTERNAL));
		new ExternalEarnOrderCall(this, blockchainSource, offerJwt, eventLogger, new ExternalOrderCallbacks() {
			@Override
			public void onOrderConfirmed(String confirmationJwt, Order order) {
				if (callback != null) {
					callback.onResponse(createOrderConfirmation(confirmationJwt));
				}
				eventLogger.send(EarnOrderCompleted
					.create(EarnOrderCompleted.OfferType.EXTERNAL, (double) order.getAmount(), order.getOfferId(),
						order.getOrderId(), EarnOrderCompleted.Origin.EXTERNAL));
			}

			@Override
			public void onOrderFailed(final String offerId, final String orderId, KinEcosystemException exception) {
				if (!StringUtil.isEmpty(orderId)) { // did not fail before submit
					decrementCount();
				}
				handleOnFailure(exception, offerId, orderId);
			}

			private void handleOnFailure(KinEcosystemException exception, final String offerId, final String orderId) {
				if (callback != null) {
					callback.onFailure(exception);
				}
				final String finalOfferId = StringUtil.safeGuardNullString(offerId);
				final String finalOrderId = StringUtil.safeGuardNullString(orderId);
				eventLogger.send(EarnOrderFailed.create(exception.getMessage(), finalOfferId, finalOrderId, EarnOrderFailed.Origin.EXTERNAL));
			}
		}).start();
	}

	private OrderConfirmation createOrderConfirmation(String confirmationJwt) {
		OrderConfirmation orderConfirmation = new OrderConfirmation();
		orderConfirmation.setStatus(OrderConfirmation.Status.COMPLETED);
		orderConfirmation.setJwtConfirmation(confirmationJwt);
		return orderConfirmation;
	}

	@Override
	public void addOrderObserver(@NonNull Observer<Order> observer) {
		getOrderWatcher().addObserver(observer);
		final Order currentOrder = getOrderWatcher().getValue();
		if (currentOrder != null) {
			observer.onChanged(currentOrder);
		}
	}

	@Override
	public void removeOrderObserver(@NonNull Observer<Order> observer) {
		getOrderWatcher().removeObserver(observer);
	}

	@Override
	public void isFirstSpendOrder(@NonNull final KinCallback<Boolean> callback) {
		localData.isFirstSpendOrder(new Callback<Boolean, Void>() {
			@Override
			public void onResponse(Boolean response) {
				callback.onResponse(response);
			}

			@Override
			public void onFailure(Void t) {
				callback.onFailure(
					ErrorUtil.getClientException(ClientException.INTERNAL_INCONSISTENCY, new DataNotAvailableException()));
			}
		});
	}

	@Override
	public void setIsFirstSpendOrder(boolean isFirstSpendOrder) {
		localData.setIsFirstSpendOrder(isFirstSpendOrder);
	}

	@Override
	public void getExternalOrderStatus(@NonNull String offerID,
		@NonNull final KinCallback<OrderConfirmation> callback) {
		remoteData
			.getFilteredOrderHistory(Origin.EXTERNAL.getValue(), offerID, new Callback<OrderList, ApiException>() {
				@Override
				public void onResponse(OrderList response) {
					if (response != null) {
						final List<Order> orders = response.getOrders();
						if (orders != null && orders.size() > 0) {
							final Order order = orders.get(orders.size() - 1);
							OrderConfirmation orderConfirmation = new OrderConfirmation();
							OrderConfirmation.Status status = OrderConfirmation.Status
								.fromValue(order.getStatus().getValue());
							orderConfirmation.setStatus(status);
							if (status == OrderConfirmation.Status.COMPLETED) {
								try {
									orderConfirmation
										.setJwtConfirmation(
											((JWTBodyPaymentConfirmationResult) order.getResult()).getJwt());
								} catch (ClassCastException e) {
									callback.onFailure(
										ErrorUtil.getClientException(ClientException.INTERNAL_INCONSISTENCY,
											new DataNotAvailableException()));
								}
							}
							callback.onResponse(orderConfirmation);
						} else {
							callback.onFailure(
								ErrorUtil.getClientException(ClientException.INTERNAL_INCONSISTENCY,
									new DataNotAvailableException()));
						}
					}
				}

				@Override
				public void onFailure(ApiException e) {
					callback.onFailure(ErrorUtil.fromApiException(e));
				}
			});
	}

	@Override
	public void logout() {
		cachedOrderList = null;
		getOrderWatcher().removeAllObservers();
		cachedOpenOrder.removeAllObservers();
		getOrderWatcher().postValue(null);
		cachedOpenOrder.postValue(null);

	}

	private void decrementCount() {
		decrementPendingOrdersCount();
		decrementPaymentObserverCount();
	}
}
