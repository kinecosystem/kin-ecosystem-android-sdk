package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;
import android.util.Log;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.SignTransactionListener;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.blockchain.Payment;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.OutgoingTransferRequest;
import com.kin.ecosystem.core.util.ExecutorsUtil.MainThreadExecutor;
import java.math.BigDecimal;
import kin.sdk.migration.common.exception.OperationFailedException;

public class OutoingTransferCall extends Thread {

	private OrderDataSource orderRepository;
	private BlockchainSource blockchainSource;
	private OutgoingTransferRequest request;
	private OutgoingTransferCallback callback;
	private Observer<Payment> paymentObserver;
	private MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

	public OutoingTransferCall(@NonNull OrderDataSource orderRepository, @NonNull BlockchainSource blockchainSource,
		@NonNull OutgoingTransferRequest request, @NonNull OutgoingTransferCallback callback) {
		this.blockchainSource = blockchainSource;
		this.request = request;
		this.orderRepository = orderRepository;
		this.callback = callback;
	}

	@Override
	public void run() {
		BlockchainSource blockchainSource = BlockchainSourceImpl.getInstance();
		OrderRepository orderRepository = OrderRepository.getInstance();

		try {
			final OpenOrder order = orderRepository.createOutgoingTransferOrderSync(request);
			payForOrder(request, order);
			Log.d("Berry", "Thread run method completed ");

		} catch (ApiException e) {
			// TODO throw KinTransferException
			//throw new KinTransferException(toAddress, e.getMessage());
		} catch (Exception e) {
			// TODO throw KinTransferException

		}
	}

	private void payForOrder(final OutgoingTransferRequest request, final OpenOrder order) {
		try {
			blockchainSource.signTransaction(request.getWalletAddress(), new BigDecimal(request.getAmount()),
				request.getMemo(), order.getOfferId(), new SignTransactionListener() {
					@Override
					public void onTransactionSigned(@NonNull String transaction) {
						createPaymentObserver(request, order, blockchainSource.extractTransactionId(transaction));
						blockchainSource.addPaymentObservable(paymentObserver);
						orderRepository.submitSpendOrder(order.getOfferId(), transaction, order.getId(),
							"Sending Kin to " + request.getAppId(),
							new KinCallback<Order>() {
								@Override
								public void onResponse(Order order) {
									Log.d("Berry", "Got order response "+order);
								}

								@Override
								public void onFailure(KinEcosystemException exception) {
									callback.onOutgoingTransferFailed(request, order, exception);
//								callback.onError(new KinTransferException(request.getWalletAddress(), ErrorUtil.getMessage(exception,
//									"Failure in submitting order with memo "+request.getMemo()+" orderId "+order.getOrderId()+", exception: "+exception.getMessage()));
								}
							});
					}
				});
			Log.d("Berry", "payForOrderCompleted");
		} catch (OperationFailedException e) {
			e.printStackTrace();
			//throw new KinTransferException("", ErrorUtil.getMessage(e, "Sign Transaction Failed"));
			// TODO convert to KinEcosystemException
		}
	}


	private void createPaymentObserver(final OutgoingTransferRequest request, final OpenOrder order,
		final String transactionId) {
		this.paymentObserver = new Observer<Payment>() {
			@Override
			public void onChanged(final Payment payment) {
				if (isMemoEqualsToPaymentMemo(payment, request.getMemo())) {
					blockchainSource.removePaymentObserver(this);
					//Cancel SSE timeout task
//					if (!isTimeoutTaskCanceled.getAndSet(true)) {
//						sseTimeoutTimer.cancel();
//					}

					if (payment.isSucceed()) {
						callback.onOutgoingTransferSucccess(request, order, transactionId);
					} else {
						// TODO
						// callback.onOutgoingTransferFailed();
					}

				}
			}
		};
	}

	private boolean isMemoEqualsToPaymentMemo(Payment payment, String memo) {
		return memo.equals(payment.getOrderID());
	}


	interface OutgoingTransferCallback {

		void onOutgoingTransferSucccess(OutgoingTransferRequest request, OpenOrder order, String transactionId);

		void onOutgoingTransferFailed(OutgoingTransferRequest request, OpenOrder order,
			KinEcosystemException exception);
	}

}
