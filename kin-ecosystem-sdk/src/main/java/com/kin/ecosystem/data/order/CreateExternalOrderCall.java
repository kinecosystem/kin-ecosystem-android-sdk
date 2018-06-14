package com.kin.ecosystem.data.order;

import android.support.annotation.NonNull;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.JWTBodyPaymentConfirmationResult;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.util.ErrorUtil;
import com.kin.ecosystem.util.ExecutorsUtil.MainThreadExecutor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

class CreateExternalOrderCall extends Thread {

	private final OrderDataSource.Remote remote;
	private final BlockchainSource blockchainSource;
	private final String orderJwt;
	private final ExternalOrderCallbacks externalOrderCallbacks;

	private OpenOrder openOrder;
	private MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

	CreateExternalOrderCall(@NonNull OrderDataSource.Remote remote, @NonNull BlockchainSource blockchainSource,
		@NonNull String orderJwt,
		@NonNull ExternalOrderCallbacks externalOrderCallbacks) {
		this.remote = remote;
		this.blockchainSource = blockchainSource;
		this.orderJwt = orderJwt;
		this.externalOrderCallbacks = externalOrderCallbacks;
	}

	@Override
	public void run() {
		try {
			// Create external order
			openOrder = remote.createExternalOrderSync(orderJwt);
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

				runOnMainThread(new Runnable() {
					@Override
					public void run() {
						externalOrderCallbacks.onOrderFailed(ErrorUtil.fromApiException(e));
					}
				});
			}
			return;
		}

		if (externalOrderCallbacks instanceof ExternalSpendOrderCallbacks) {
			// Send transaction to the network.
			blockchainSource.sendTransaction(openOrder.getBlockchainData().getRecipientAddress(),
				new BigDecimal(openOrder.getAmount()), openOrder.getId());

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
										.onTransactionFailed(openOrder, ErrorUtil.getBlockchainException(payment.getException()));
								}
							});
						}
					}
					blockchainSource.removePaymentObserver(this);
				}
			}
		});
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
							.onOrderConfirmed(((JWTBodyPaymentConfirmationResult) order.getResult()).getJwt());
					}
				});

			}

			@Override
			public void onFailure(final ApiException e) {
				runOnMainThread(new Runnable() {
					@Override
					public void run() {
						externalOrderCallbacks
							.onOrderFailed(ErrorUtil.fromApiException(e));
					}
				});
			}
		}).start();
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

		void onOrderConfirmed(String confirmationJwt);

		void onOrderFailed(KinEcosystemException exception);
	}

	interface ExternalSpendOrderCallbacks extends ExternalOrderCallbacks {

		void onTransactionSent(OpenOrder openOrder);

		void onTransactionFailed(OpenOrder openOrder, KinEcosystemException exception);
	}
}
