package com.kin.ecosystem.data.blockchain;

import kin.core.KinAccount;
import kin.core.exception.OperationFailedException;

class CreateTrustLineCall extends Thread {

	private final KinAccount account;
	private final TrustlineCallback trustlineCallback;

	private static final int MAX_TRIES = 10;

	CreateTrustLineCall(KinAccount account, TrustlineCallback trustlineCallback) {
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
			if (tries < MAX_TRIES) {
				createTrustline(++tries);
			} else {
				trustlineCallback.onFailure(e);
			}
		}
	}

	interface TrustlineCallback {

		void onSuccess();

		void onFailure(OperationFailedException e);
	}
}
