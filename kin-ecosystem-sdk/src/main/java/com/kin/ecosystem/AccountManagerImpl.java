package com.kin.ecosystem;

import android.support.annotation.NonNull;
import com.kin.ecosystem.CreateTrustLineCall.TrustlineCallback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.StellarAccountCreationRequested;
import com.kin.ecosystem.bi.events.StellarKinTrustlineSetupFailed;
import com.kin.ecosystem.bi.events.StellarKinTrustlineSetupSucceeded;
import com.kin.ecosystem.bi.events.WalletCreationSucceeded;
import com.kin.ecosystem.data.KinCallbackAdapter;
import com.kin.ecosystem.data.auth.AuthDataSource;
import com.kin.ecosystem.network.model.AuthToken;
import kin.core.AccountStatus;
import kin.core.EventListener;
import kin.core.KinAccount;
import kin.core.ListenerRegistration;
import kin.core.exception.OperationFailedException;

class AccountManagerImpl implements AccountManager {

	private static final String TAG = AccountManagerImpl.class.getSimpleName();

	private final AccountManager.Local local;
	private final EventLogger eventLogger;
	private AuthDataSource authRepository;
	private KinAccount kinAccount;
	private final ObservableData<Integer> accountState;

	private ListenerRegistration accountCreationRegistration;

	AccountManagerImpl(@NonNull final AccountManager.Local local, @NonNull final EventLogger eventLogger) {
		this.local = local;
		this.eventLogger = eventLogger;
		this.accountState = ObservableData.create(local.getAccountState());
	}

	@Override
	public @AccountState
	int getAccountState() {
		return local.getAccountState();
	}

	@Override
	public boolean isAccountCreated() {
		return local.getAccountState() == CREATION_COMPLETED;
	}

	@Override
	public void addAccountStateObserver(@NonNull Observer<Integer> observer) {
		accountState.addObserver(observer);
		accountState.postValue(local.getAccountState());
	}

	@Override
	public void removeAccountStateObserver(@NonNull Observer<Integer> observer) {
		accountState.removeObserver(observer);
	}


	void start(@NonNull final AuthDataSource authRepository, @NonNull final KinAccount kinAccount) {
		Logger.log(new Log().withTag(TAG).put("setAccountState", "start"));
		this.authRepository = authRepository;
		this.kinAccount = kinAccount;

		if (getAccountState() != CREATION_COMPLETED) {
			this.setAccountState(getAccountState());
		} else {
			accountState.postValue(getAccountState());
		}
	}

	private void setAccountState(@AccountState final int accountState) {
		if (isValidState(this.accountState.getValue(), accountState)) {
			this.local.setAccountState(accountState);
			this.accountState.postValue(accountState);
			switch (accountState) {
				case REQUIRE_CREATION:
					eventLogger.send(StellarAccountCreationRequested.create());
					Logger.log(new Log().withTag(TAG).put("setAccountState", "REQUIRE_CREATION"));
					// Trigger account creation from server side.
					authRepository.getAuthToken(new KinCallbackAdapter<AuthToken>() {
						@Override
						public void onResponse(AuthToken response) {
							setAccountState(PENDING_CREATION);
						}
					});
					break;
				case PENDING_CREATION:
					Logger.log(new Log().withTag(TAG).put("setAccountState", "PENDING_CREATION"));
					// Start listen for account creation on the blockchain side.
					accountCreationRegistration = kinAccount.blockchainEvents()
						.addAccountCreationListener(new EventListener<Void>() {
							@Override
							public void onEvent(Void data) {
								accountCreationRegistration.remove();
								accountCreationRegistration = null;
								setAccountState(REQUIRE_TRUSTLINE);
							}
						});
					break;
				case REQUIRE_TRUSTLINE:
					Logger.log(new Log().withTag(TAG).put("setAccountState", "REQUIRE_TRUSTLINE"));
					// Create trustline transaction with KIN
					new CreateTrustLineCall(kinAccount, new TrustlineCallback() {
						@Override
						public void onSuccess() {
							eventLogger.send(StellarKinTrustlineSetupSucceeded.create());
							setAccountState(CREATION_COMPLETED);
						}

						@Override
						public void onFailure(OperationFailedException e) {
							eventLogger.send(StellarKinTrustlineSetupFailed.create(e.getMessage()));
							setAccountState(REQUIRE_TRUSTLINE);
						}
					}).start();
					break;
				case CREATION_COMPLETED:
					// Mark account creation completed.
					eventLogger.send(WalletCreationSucceeded.create());
					Logger.log(new Log().withTag(TAG).put("setAccountState", "CREATION_COMPLETED"));
					break;
				default:
					break;
			}
		}
	}

	private boolean isValidState(int currentState, int newState) {
		return newState >= currentState;
	}

}
