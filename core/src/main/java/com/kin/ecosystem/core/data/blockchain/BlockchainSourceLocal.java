package com.kin.ecosystem.core.data.blockchain;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlockchainSourceLocal implements BlockchainSource.Local {

	static final int NOT_EXIST = -1;
	private static volatile BlockchainSourceLocal instance;

	private static final String BLOCKCHAIN_PREF_NAME_FILE_KEY = "kinecosystem_blockchain_source";
	private static final String BALANCE_KEY = "balance_key";
	private static final String ACCOUNT_INDEX_KEY = "account_index_key";
	private static final String CURRENT_KIN_USER_ID = "current_kin_user_id";
	private static final String CURRENT_WALLET_ADDRESS = "current_wallet_address";

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

	@Nullable
	@Override
	public String getCurrentWalletAddress() {
		return blockchainSharedPreferences.getString(CURRENT_WALLET_ADDRESS, null);
	}

	@Override
	public int getAccountIndex() {
		return blockchainSharedPreferences.contains(ACCOUNT_INDEX_KEY) ? blockchainSharedPreferences.getInt(ACCOUNT_INDEX_KEY, 0) : NOT_EXIST;
	}

	@Override
	public void setActiveUserWallet(String userId, String publicAddress) {
		Set<String> currentWallets = getUserWallets(userId);
		Set<String> updatedWallets = currentWallets != null ? new LinkedHashSet<>(currentWallets) : new LinkedHashSet<String>();
		updatedWallets.remove(publicAddress); // Remove if exists
		updatedWallets.add(publicAddress); // Add to the end
		Editor editor = blockchainSharedPreferences.edit();
		editor.putString(CURRENT_KIN_USER_ID, userId);
		editor.putString(CURRENT_WALLET_ADDRESS, publicAddress);
		editor.putStringSet(userId, updatedWallets).apply();
		editor.apply();
	}

	@Override
	@Nullable
	public Set<String> getUserWallets(String userId) {
		return blockchainSharedPreferences.getStringSet(userId, null);
	}

	@Override
	public void deleteAccountIndexKey() {
		blockchainSharedPreferences.edit().remove(ACCOUNT_INDEX_KEY).apply();
	}
}
