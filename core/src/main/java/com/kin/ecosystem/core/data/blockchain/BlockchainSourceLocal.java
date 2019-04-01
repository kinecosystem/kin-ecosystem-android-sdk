package com.kin.ecosystem.core.data.blockchain;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kin.sdk.migration.common.KinSdkVersion;

public class BlockchainSourceLocal implements BlockchainSource.Local {

	static final int NOT_EXIST = -1;
	private static final String STRING_WALLETS_DELIMITER = ",";
	private static volatile BlockchainSourceLocal instance;

	private static final String BLOCKCHAIN_PREF_NAME_FILE_KEY = "kinecosystem_blockchain_source";
	private static final String BALANCE_KEY = "balance_key";
	private static final String ACCOUNT_INDEX_KEY = "account_index_key";
	private static final String CURRENT_KIN_USER_ID = "current_kin_user_id";
	private static final String IS_MIGRATED_KEY = "is_migrated_key";
	private static final String BLOCKCHAIN_VERSION = "blockchain_version";

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
	public String getLastWalletAddress(final String kinUserId) {
		ArrayList<String> wallets = getUserWallets(kinUserId);
		return wallets.size() > 0 ? wallets.get(wallets.size() - 1) : null;
	}

	@Override
	public int getAccountIndex() {
		return blockchainSharedPreferences.contains(ACCOUNT_INDEX_KEY) ? blockchainSharedPreferences
			.getInt(ACCOUNT_INDEX_KEY, 0) : NOT_EXIST;
	}

	@Override
	public void setActiveUserWallet(String kinUserId, String publicAddress) {
		ArrayList<String> currentWallets = getUserWallets(kinUserId);
		currentWallets.remove(publicAddress); // Remove if exists
		currentWallets.add(publicAddress); // Add to the end
		Editor editor = blockchainSharedPreferences.edit();
		editor.putString(CURRENT_KIN_USER_ID, kinUserId);
		editor.putString(kinUserId, createLongString(currentWallets));
		editor.apply();
	}

	private String createLongString(List<String> currentWallets) {
		StringBuilder walletsStringBuilder = new StringBuilder();
		for (int i = 0; i < currentWallets.size(); i++) {
			walletsStringBuilder.append(currentWallets.get(i));
			if (i < currentWallets.size() - 1) {
				walletsStringBuilder.append(STRING_WALLETS_DELIMITER);
			}
		}
		return walletsStringBuilder.toString();
	}

	private ArrayList<String> getUserWallets(String kinUserId) {
		String walletsString = blockchainSharedPreferences.getString(kinUserId, null);
		if (walletsString != null) {
			return new ArrayList<>(Arrays.asList(walletsString.split(STRING_WALLETS_DELIMITER)));
		}
		return new ArrayList<>();
	}

	@Override
	public void removeAccountIndexKey() {
		blockchainSharedPreferences.edit().remove(ACCOUNT_INDEX_KEY).apply();
	}

	@Override
	public void logout() {
		blockchainSharedPreferences.edit().remove(BALANCE_KEY).apply();
	}

	@Override
	public boolean getIsMigrated() {
		return blockchainSharedPreferences.getBoolean(IS_MIGRATED_KEY, false);
	}

	@Override
	public void setDidMigrate() {
		blockchainSharedPreferences.edit().putBoolean(IS_MIGRATED_KEY, true).apply();
	}

	@Override
	public KinSdkVersion getBlockchainVersion() {
		String version = blockchainSharedPreferences.getString(BLOCKCHAIN_VERSION, KinSdkVersion.OLD_KIN_SDK.getVersion());
		return KinSdkVersion.get(version);
	}

	@Override
	public void setBlockchainVersion(KinSdkVersion version) {
		blockchainSharedPreferences.edit().putString(BLOCKCHAIN_VERSION, version.getVersion()).apply();
	}
}
