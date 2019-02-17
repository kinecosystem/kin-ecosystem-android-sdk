package com.kin.ecosystem.base;

import android.content.Context;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import kin.core.KinAccount;

final class KinEcosystemInitiator {

	private static final String TAG = "KinEcosystemAutoInit";

	private KinEcosystemInitiator() {
	}

	protected static void init(Context context) {
		try {
			Kin.initialize(context);
			// If we had process restart we should load the account.
			KinAccount account = BlockchainSourceImpl.getInstance().getKinAccount();
			if (account == null) {
				final String kinUserId = AuthRepository.getInstance().getEcosystemUserID();
				BlockchainSourceImpl.getInstance().loadAccount(kinUserId);
			}
		} catch (ClientException | BlockchainException e) {
			Logger.log(new Log().withTag(TAG).text("KinEcosystem sdk auto initialize failed"));
		}
	}
}
