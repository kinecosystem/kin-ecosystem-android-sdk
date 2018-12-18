package com.kin.ecosystem.base;

import android.content.Context;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;

final class KinEcosystemInitiator {

	private static final String TAG = "KinEcosystemAutoInit";

	private KinEcosystemInitiator() {
	}

	protected static void init(Context context) {
		try {
			Kin.initialize(context);
		} catch (ClientException e) {
			Logger.log(new Log().withTag(TAG).text("KinEcosystem sdk auto initialize failed"));
		}
	}
}
