package com.kin.ecosystem;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import com.kin.ecosystem.base.Observer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface AccountManager {

	int NONE = 0x00000001;
	int REQUIRE_CREATION = 0x00000002;
	int PENDING_CREATION = 0x00000003;
	int REQUIRE_TRUSTLINE = 0x00000004;
	int CREATION_COMPLETED = 0x00000005;

	@IntDef({REQUIRE_CREATION, PENDING_CREATION,
		REQUIRE_TRUSTLINE, CREATION_COMPLETED})
	@Retention(RetentionPolicy.SOURCE)
	@interface AccountState {

	}

	@AccountState
	int getAccountState();

	void addAccountStateObserver(@NonNull final Observer<Integer> observer);

	void removeAccountStateObserver(@NonNull final Observer<Integer> observer);

	interface Local {

		int getAccountState();

		void setAccountState(@AccountState int accountState);
	}
}
