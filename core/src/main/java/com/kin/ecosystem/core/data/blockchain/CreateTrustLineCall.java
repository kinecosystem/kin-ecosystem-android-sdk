package com.kin.ecosystem.core.data.blockchain;

import kin.sdk.migration.common.exception.OperationFailedException;
import kin.sdk.migration.common.interfaces.IKinAccount;

public class CreateTrustLineCall extends Thread {

	private static final int[] DELAY_SECONDS = {2, 4, 8, 16, 32, 32, 32, 32, 32, 32};
	private static final int SEC_IN_MILLI = 1000;

	private final IKinAccount account;
	private final TrustlineCallback trustlineCallback;

	public CreateTrustLineCall(IKinAccount account, TrustlineCallback trustlineCallback) {
		this.account = account;
		this.trustlineCallback = trustlineCallback;
	}

	@Override
	public void run() {
		super.run();
		createTrustline(0);
	}

	private void createTrustline(int tries) {
		try {
			account.activateSync();
			trustlineCallback.onSuccess();
		} catch (OperationFailedException e) {
			if (tries < DELAY_SECONDS.length) {
				try {
					sleep(DELAY_SECONDS[tries] * SEC_IN_MILLI);
					createTrustline(++tries);
				} catch (InterruptedException e1) {
					trustlineCallback.onFailure(new OperationFailedException(e1));
				}
			} else {
				trustlineCallback.onFailure(e);
			}
		}
	}

	public interface TrustlineCallback {

		void onSuccess();

		void onFailure(OperationFailedException e);
	}
}
