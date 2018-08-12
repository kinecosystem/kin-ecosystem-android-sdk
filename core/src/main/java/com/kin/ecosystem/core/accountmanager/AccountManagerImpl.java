package com.kin.ecosystem.core.accountmanager;

import android.support.annotation.NonNull;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.StellarAccountCreationRequested;
import com.kin.ecosystem.core.bi.events.StellarKinTrustlineSetupFailed;
import com.kin.ecosystem.core.bi.events.StellarKinTrustlineSetupSucceeded;
import com.kin.ecosystem.core.bi.events.WalletCreationSucceeded;
import com.kin.ecosystem.core.data.auth.AuthDataSource;
import com.kin.ecosystem.core.data.blockchain.CreateTrustLineCall;
import com.kin.ecosystem.core.data.blockchain.CreateTrustLineCall.TrustlineCallback;
import com.kin.ecosystem.core.network.model.AuthToken;
import kin.core.EventListener;
import kin.core.KinAccount;
import kin.core.ListenerRegistration;
import kin.core.exception.OperationFailedException;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;

public class AccountManagerImpl implements AccountManager {

	private static final String TAG = AccountManagerImpl.class.getSimpleName();

	private static volatile AccountManagerImpl instance;

	private final AccountManager.Local local;
	private final EventLogger eventLogger;
	private AuthDataSource authRepository;
	private KinAccount kinAccount;
	private final ObservableData<Integer> accountState;

	private ListenerRegistration accountCreationRegistration;

	private AccountManagerImpl(@NonNull final AccountManager.Local local,
		@NonNull final EventLogger eventLogger,
		@NonNull final AuthDataSource authRepository) {
		this.local = local;
		this.eventLogger = eventLogger;
		this.authRepository = authRepository;
		this.accountState = ObservableData.create(local.getAccountState());
	}

	public static void init(@NonNull final AccountManager.Local local,
		@NonNull final EventLogger eventLogger,
		@NonNull final AuthDataSource authRepository) {
		if (instance == null) {
			synchronized (AccountManagerImpl.class) {
				if (instance == null) {
					instance = new AccountManagerImpl(local, eventLogger, authRepository);
				}
			}
		}
	}

	public static AccountManagerImpl getInstance() {
		return instance;
	}

	@Override
	public @AccountState
	int getAccountState() {
		return accountState.getValue() == ERROR ? ERROR : local.getAccountState();
	}

	@Override
	public boolean isAccountCreated() {
		return local.getAccountState() == CREATION_COMPLETED;
	}

	@Override
	public void addAccountStateObserver(@NonNull Observer<Integer> observer) {
		accountState.addObserver(observer);
		accountState.postValue(accountState.getValue());
	}

	@Override
	public void removeAccountStateObserver(@NonNull Observer<Integer> observer) {
		accountState.removeObserver(observer);
	}

	@Override
	public void retry() {
		if (kinAccount != null && accountState.getValue() == ERROR) {
			setAccountState(local.getAccountState());
		}
	}

	@Override
	public void start(@NonNull final KinAccount kinAccount) {
		this.kinAccount = kinAccount;
		Logger.log(new Log().withTag(TAG).put("setAccountState", "start"));
		if (getAccountState() != CREATION_COMPLETED) {
			this.setAccountState(local.getAccountState());
		}
	}

	private void setAccountState(@AccountState final int accountState) {
		if (isValidState(this.accountState.getValue(), accountState)) {
			if (accountState != ERROR) {
				this.local.setAccountState(accountState);
			}
			this.accountState.postValue(accountState);
			switch (accountState) {
				case REQUIRE_CREATION:
					eventLogger.send(StellarAccountCreationRequested.create());
					Logger.log(new Log().withTag(TAG).put("setAccountState", "REQUIRE_CREATION"));
					// Trigger account creation from server side.
					authRepository.getAuthToken(new KinCallback<AuthToken>() {
						@Override
						public void onResponse(AuthToken response) {
							setAccountState(PENDING_CREATION);
						}

						@Override
						public void onFailure(KinEcosystemException error) {
							setAccountState(ERROR);
						}
					});
					break;
				case PENDING_CREATION:
					Logger.log(new Log().withTag(TAG).put("setAccountState", "PENDING_CREATION"));
					// Start listen for account creation on the blockchain side.
					if (accountCreationRegistration != null) {
						removeAccountCreationRegistration();
					}
					accountCreationRegistration = kinAccount.blockchainEvents()
						.addAccountCreationListener(new EventListener<Void>() {
							@Override
							public void onEvent(Void data) {
								removeAccountCreationRegistration();
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
							setAccountState(ERROR);
						}
					}).start();
					break;
				case CREATION_COMPLETED:
					// Mark account creation completed.
					eventLogger.send(WalletCreationSucceeded.create());
					Logger.log(new Log().withTag(TAG).put("setAccountState", "CREATION_COMPLETED"));
					break;
				default:
				case AccountManager.ERROR:
					Logger.log(new Log().withTag(TAG).put("setAccountState", "ERROR"));
					break;

			}
		}
	}

	private void removeAccountCreationRegistration() {
		accountCreationRegistration.remove();
		accountCreationRegistration = null;
	}

	private boolean isValidState(int currentState, int newState) {
		return newState == ERROR || currentState == ERROR || newState >= currentState;
	}

}
