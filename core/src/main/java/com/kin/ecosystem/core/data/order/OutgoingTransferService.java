package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;

import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.order.OutoingTransferCall.OutgoingTransferCallback;
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

		// TODO add the correct title & description in strings.xml and use it
		OutgoingTransferRequest request = new OutgoingTransferRequest()
			.amount(amount)
			.appId(receiverAppId)
			.title("Transfer to " + receiverAppName)
			.description("Detailed description")
			.memo(memo)
			.walletAddress(toAddress);

		new OutoingTransferCall(OrderRepository.getInstance(), BlockchainSourceImpl.getInstance(), request,
			new OutgoingTransferCallback() {
				@Override
				public void onOutgoingTransferSucccess(OutgoingTransferRequest request, OpenOrder order,
					String transactionId) {
					callback.onSuccess(new KinTransferComplete(request.getWalletAddress(), transactionId, request.getMemo()));
				}

				@Override
				public void onOutgoingTransferFailed(OutgoingTransferRequest request, OpenOrder order,
					KinEcosystemException exception) {
					callback.onError(new KinTransferException(request.getWalletAddress(), "error"));
				}
			}).start();
	}

	@Override
	public BigDecimal getCurrentBalance() throws BalanceException {
		try {
			//TODO maybe use cached balance instead of real
			return BlockchainSourceImpl.getInstance().getBalanceSync().getAmount();
		} catch (Exception e) {
			// TODO use ErrorUtil
			throw new BalanceException(
				"Exception " + e + " ocurred while retrieving users balance. Message: " + e.getMessage());
		}
	}
}
