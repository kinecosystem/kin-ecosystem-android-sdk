package com.kin.ecosystem;

import static com.kin.ecosystem.AccountManager.REQUIRE_CREATION;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.kin.ecosystem.AccountManager.AccountState;

class AccountManagerLocal implements AccountManager.Local {

	private static volatile AccountManagerLocal instance;

	private static final String ACCOUNT_MANAGER_PREF_NAME_FILE_KEY = "kinecosystem_account_manager";

	private static final String ACCOUNT_STATE_KEY = "account_state";

	private final SharedPreferences accountStateSharedPref;

	private AccountManagerLocal(@NonNull Context context) {
		this.accountStateSharedPref = context
			.getSharedPreferences(ACCOUNT_MANAGER_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE);
	}

	public static AccountManagerLocal getInstance(@NonNull Context context) {
		if (instance == null) {
			synchronized (AccountManagerLocal.class) {
				if (instance == null) {
					instance = new AccountManagerLocal(context);
				}
			}
		}

		return instance;
	}

	@Override
	public int getAccountState() {
		return accountStateSharedPref.getInt(ACCOUNT_STATE_KEY, REQUIRE_CREATION);
	}

	@Override
	public void setAccountState(@AccountState int accountState) {
		accountStateSharedPref.edit().putInt(ACCOUNT_STATE_KEY, accountState).apply();
	}
}
