package com.kin.ecosystem.core.accountmanager;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface AccountManager {

	int REQUIRE_CREATION = 0x00000001;
	int PENDING_CREATION = 0x00000002;
	int REQUIRE_TRUSTLINE = 0x00000003;
	int CREATION_COMPLETED = 0x00000004;
	int ERROR = 0x00000005;



	@IntDef({REQUIRE_CREATION,
		PENDING_CREATION,
		REQUIRE_TRUSTLINE,
		CREATION_COMPLETED,
		ERROR})
	@Retention(RetentionPolicy.SOURCE)
	@interface AccountState {


	}
	void start();

	void retry();

	void logout();

	void switchAccount(int accountIndex, @NonNull final KinCallback<Boolean> callback);

	@AccountState
	int getAccountState();

	boolean isAccountCreated();

	void addAccountStateObserver(@NonNull final Observer<Integer> observer);

	void removeAccountStateObserver(@NonNull final Observer<Integer> observer);

	@Nullable
	KinEcosystemException getError();

	interface Local {

		int getAccountState();

		void setAccountState(@AccountState int accountState);

		void logout();
	}
}
