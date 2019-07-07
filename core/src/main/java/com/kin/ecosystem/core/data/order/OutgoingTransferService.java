package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;

import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.order.OutgoingTransferCall.OutgoingTransferCallback;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.OutgoingTransferRequest;

import org.kinecosystem.transfer.repositories.KinTransferCallback;
import org.kinecosystem.transfer.sender.service.SendKinServiceBase;

import java.math.BigDecimal;

public class OutgoingTransferService extends SendKinServiceBase {

	@Override
	public KinTransferComplete transferKin(final @NonNull String receiverAppId, final @NonNull String receiverAppName,
		final @NonNull String toAddress, final int amount, final @NonNull String memo) throws KinTransferException {
		// returning null so that the async method will be called
		return null;
	}

	@Override
	public void transferKinAsync(@NonNull final String receiverAppId, @NonNull final String receiverAppName,
		final @NonNull String toAddress, final int amount, @NonNull final String memo, @NonNull final KinTransferCallback callback) {
		String transferInfo = "Sent to " + receiverAppName;
		String description = "Transferred on";
		OutgoingTransferRequest request = new OutgoingTransferRequest()
			.amount(amount)
			.appId(receiverAppId)
			.title(transferInfo)
			.description(description)
			.memo(memo)
			.walletAddress(toAddress);

		new OutgoingTransferCall(request, transferInfo,
			new OutgoingTransferCallback() {
				@Override
				public void onOutgoingTransferSuccess(OutgoingTransferRequest request, OpenOrder order,
													  String transactionId) {
					callback.onSuccess(new KinTransferComplete(request.getWalletAddress(), transactionId, request.getMemo()));
				}

				@Override
				public void onOutgoingTransferFailed(OutgoingTransferRequest request, String errorMessage) {
					callback.onError(new KinTransferException(request.getWalletAddress(), errorMessage));
				}
			}).start();
	}

	@Override
	public BigDecimal getCurrentBalance() throws BalanceException {
		//need to get the updated balance every call not the cached
		try {
			return BlockchainSourceImpl.getInstance().getBalanceSync().getAmount();
		} catch (Exception e) {
			throw new BalanceException(
				"Exception " + e + " occurred while retrieving users balance. Message: " + e.getMessage());
		}
	}

}
