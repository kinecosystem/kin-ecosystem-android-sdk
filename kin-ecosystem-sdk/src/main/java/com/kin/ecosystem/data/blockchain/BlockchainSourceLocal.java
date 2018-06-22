package com.kin.ecosystem.data.blockchain;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class BlockchainSourceLocal implements BlockchainSource.Local {

	private static volatile BlockchainSourceLocal instance;

	private static final String BLOCKCHAIN_PREF_NAME_FILE_KEY = "kinecosystem_blockchain_source";

	private static final String BALANCE_KEY = "balance_key";
	private static final String HAS_TRUSTLINE_KEY = "has_trustline_key";

	private final SharedPreferences blockchainSharedPreferences;


	private BlockchainSourceLocal(@NonNull final Context context) {
		this.blockchainSharedPreferences = context
			.getSharedPreferences(BLOCKCHAIN_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE);
	}

	public static BlockchainSourceLocal getInstance(@NonNull final Context context) {
		if (instance == null) {
			synchronized (BlockchainSourceLocal.class) {
				if (instance == null) {
					instance = new BlockchainSourceLocal(context);
				}
			}
		}
		return instance;
	}


	@Override
	public int getBalance() {
		return blockchainSharedPreferences.getInt(BALANCE_KEY, 0);
	}

	@Override
	public void setBalance(int balance) {
		blockchainSharedPreferences.edit().putInt(BALANCE_KEY, balance).apply();
	}

	@Override
	public boolean hasTrustLine() {
		return blockchainSharedPreferences.getBoolean(HAS_TRUSTLINE_KEY, false);
	}

	@Override
	public void setHasTrustline(boolean hasTrustline) {
		 blockchainSharedPreferences.edit().putBoolean(HAS_TRUSTLINE_KEY, hasTrustline).apply();
	}
}
