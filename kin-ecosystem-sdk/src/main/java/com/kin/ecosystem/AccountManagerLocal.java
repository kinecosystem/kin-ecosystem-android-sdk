package com.kin.ecosystem;

import static com.kin.ecosystem.AccountManager.REQUIRE_CREATION;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.kin.ecosystem.AccountManager.AccountState;

class AccountManagerLocal implements AccountManager.Local {

	private static final String ACC_MANAGER_PREF_NAME_FILE_KEY = "kinecosystem_account_manager";

	private static final String ACC_STATE_KEY = "account_state";

	private final SharedPreferences accountStateSharedPref;

	AccountManagerLocal(@NonNull Context context) {
		this.accountStateSharedPref = context
			.getSharedPreferences(ACC_MANAGER_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE);
	}

	@Override
	public int getAccountState() {
		return accountStateSharedPref.getInt(ACC_STATE_KEY, REQUIRE_CREATION);
	}

	@Override
	public void setAccountState(@AccountState int accountState) {
		accountStateSharedPref.edit().putInt(ACC_STATE_KEY, accountState).apply();
	}
}
