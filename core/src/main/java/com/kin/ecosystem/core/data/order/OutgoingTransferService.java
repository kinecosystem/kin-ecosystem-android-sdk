package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;
import android.util.Log;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.SignTransactionListener;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.OutgoingTransferRequest;
import com.kin.ecosystem.core.util.ErrorUtil;
import java.math.BigDecimal;
import kin.sdk.migration.common.exception.OperationFailedException;
import org.kinecosystem.transfer.sender.service.SendKinServiceBase;

public class OutgoingTransferService extends SendKinServiceBase {

	BlockchainSource blockchainSource;
	OrderRepository orderRepository;

	@Override
	public void onCreate() {
		super.onCreate();
		blockchainSource = BlockchainSourceImpl.getInstance();
		orderRepository = OrderRepository.getInstance();
	}

	@Override
	public KinTransferComplete transferKin(final @NonNull String receiverAppId, final @NonNull String receiverAppName,
		final @NonNull String toAddress, final int amount, final @NonNull String memo) throws KinTransferException {
		OutgoingTransferRequest request = new OutgoingTransferRequest()
			.amount(amount)
			.appId(receiverAppId)
			.description("Transfer to " + receiverAppName)
			.memo(memo)
			.title("Transfer to " + receiverAppName)
			.walletAddress(toAddress);
		try {
			final OpenOrder order = orderRepository.createOutgoingTransferOrderSync(request);
			payForOrder(toAddress, new BigDecimal(amount), memo, order.getId(), order.getOfferId(), receiverAppId);
			return new KinTransferComplete(blockchainSource.getPublicAddress(), order.getId(), memo);
		} catch (Exception e) {
			throw new KinTransferException(toAddress, e.getMessage());
		}
	}

	@Override
	public BigDecimal getCurrentBalance() throws BalanceException {
		try {
			return blockchainSource.getBalanceSync().getAmount();
		} catch (Exception e) {
			throw new BalanceException(
				"Exception " + e + " ocurred while retrieving users balance. Message: " + e.getMessage());
		}
	}


	private void payForOrder(final String address, final BigDecimal amount, final String memo, final String orderId,
		final String offerId, final String receiverAppId) {
		try {
			blockchainSource.signTransaction(address, amount, memo, offerId, new SignTransactionListener() {
				@Override
				public void onTransactionSigned(@NonNull String transaction) {
					orderRepository.submitSpendOrder(offerId, transaction, orderId, "Sending Kin to " + receiverAppId,
						new KinCallback<Order>() {
							@Override
							public void onResponse(Order order) {
								onSubmissionSucceed(order.getOrderId());
							}

							@Override
							public void onFailure(KinEcosystemException exception) {
								onSubmissionFailed(offerId, memo, exception);
							}
						});
				}
			});
		} catch (OperationFailedException e) {
			final KinEcosystemException exception = new KinEcosystemException(
				BlockchainException.SIGN_TRANSACTION_FAILED,
				ErrorUtil.getMessage(e, "Sign Transaction Failed"), e);
			onSubmissionFailed(offerId, memo, exception);
		}
	}

	private void onSubmissionSucceed(String orderId) {
		Log.d("Berry", "submission for " + orderId + " succeeded");
	}

	private void onSubmissionFailed(String offerId, String orderId, KinEcosystemException exception) {
		Log.e("Berry", "submission for " + orderId + " failed");
		exception.printStackTrace();
	}
}
