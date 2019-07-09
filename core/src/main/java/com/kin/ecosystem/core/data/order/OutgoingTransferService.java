package com.kin.ecosystem.core.data.order;


import android.support.annotation.NonNull;
import android.util.Log;

import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.order.OutgoingTransferCall.OutgoingTransferCallback;
import com.kin.ecosystem.core.network.model.IncomingTransfer;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.OutgoingTransfer;

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
		String transferTitle = "Sent to " + receiverAppName;
		String description = "Transferred on";
		OutgoingTransfer payload = new OutgoingTransfer()
			.amount(amount)
			.appId(receiverAppId)
			.title(transferTitle)
			.description(description)
			.memo(memo)
			.walletAddress(toAddress);

		new OutgoingTransferCall(BlockchainSourceImpl.getInstance(), OrderRepository.getInstance(), payload, transferTitle,
			new OutgoingTransferCallback() {
				@Override
				public void onOutgoingTransferSuccess(OutgoingTransfer payload, OpenOrder order,
													  String transactionId) {
					callback.onSuccess(new KinTransferComplete(payload.getWalletAddress(), transactionId, payload.getMemo()));
				}

				@Override
				public void onOutgoingTransferFailed(OutgoingTransfer payload, KinEcosystemException exception) {
					callback.onError(new KinTransferException(payload.getWalletAddress(), exception.getMessage()));
				}
			}).start();
	}

	@Override
	public BigDecimal getCurrentBalance() throws BalanceException {
		//need to get the updated balance every call not the cached
		try {
			return BlockchainSourceImpl.getInstance().getBalanceSync().getAmount();
		} catch (KinEcosystemException e) {
			throw new BalanceException("Exception " + e + " occurred while retrieving users balance. Message: " + e.getMessage());
		}
	}

	@Override
	public String getAddress() throws AccountException {
		try {
			return BlockchainSourceImpl.getInstance().getPublicAddress();
		} catch (BlockchainException e) {
			throw new BalanceException("Exception " + e + " occurred while retrieving users address. Message: " + e.getMessage());
		}
	}

}
