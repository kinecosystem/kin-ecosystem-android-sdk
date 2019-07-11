package com.kin.ecosystem.core.accountmanager;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.StellarAccountCreationRequested;
import com.kin.ecosystem.core.bi.events.WalletCreationSucceeded;
import com.kin.ecosystem.core.data.auth.AuthDataSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.MigrationProcessListener;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.MigrationInfo;
import com.kin.ecosystem.core.util.ErrorUtil;
import kin.sdk.migration.common.exception.DeleteAccountException;
import kin.sdk.migration.common.interfaces.IKinAccount;
import kin.sdk.migration.common.interfaces.IListenerRegistration;

public class AccountManagerImpl implements AccountManager {

	private static final String TAG = AccountManagerImpl.class.getSimpleName();

	private static volatile AccountManagerImpl instance;

	private final AccountManager.Local local;
	private final EventLogger eventLogger;
	private AuthDataSource authRepository;
	private BlockchainSource blockchainSource;
	private final ObservableData<Integer> accountState;
	private KinEcosystemException error;

	private IListenerRegistration accountCreationRegistration;

	private AccountManagerImpl(@NonNull final AccountManager.Local local,
		@NonNull final EventLogger eventLogger,
		@NonNull final AuthDataSource authRepository,
		@NonNull final BlockchainSource blockchainSource) {
		this.local = local;
		this.eventLogger = eventLogger;
		this.authRepository = authRepository;
		this.blockchainSource = blockchainSource;
		this.accountState = ObservableData.create(local.getAccountState());
	}

	public static void init(@NonNull final AccountManager.Local local,
		@NonNull final EventLogger eventLogger,
		@NonNull final AuthDataSource authRepository,
		@NonNull final BlockchainSource blockchainSource) {
		if (instance == null) {
			synchronized (AccountManagerImpl.class) {
				if (instance == null) {
					instance = new AccountManagerImpl(local, eventLogger, authRepository, blockchainSource);
				}
			}
		}
	}

	public static AccountManager getInstance() {
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
	public KinEcosystemException getError() {
		return error;
	}

	@Override
	public void retry() {
		if (getKinAccount() != null && accountState.getValue() == ERROR) {
			this.setAccountState(local.getAccountState());
		}
	}

	@Override
	public void logout() {
		removeAccountCreationRegistration();
		accountState.removeAllObservers();
		accountState.postValue(REQUIRE_CREATION);
		local.logout();
	}

	@Override
	public void start() {
		if (getKinAccount() != null && !isAccountCreated()) {
			Logger.log(new Log().withTag(TAG).put("setAccountState", "start"));
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
					setAccountState(PENDING_CREATION);
					break;
				case PENDING_CREATION:
					Logger.log(new Log().withTag(TAG).put("setAccountState", "PENDING_CREATION"));
					// Start listen for account creation on the blockchain side.
					blockchainSource.isAccountCreated(new KinCallback<Void>() {
						@Override
						public void onResponse(Void response) {
							setAccountState(CREATION_COMPLETED);
						}

						@Override
						public void onFailure(KinEcosystemException exception) {
							instance.error = exception;
							setAccountState(ERROR);
						}
					});
					break;
				case REQUIRE_TRUSTLINE:
					///////////////////////////////////////////////////////////////////////////////////////////////
					//  Deprecated this is NOT part of the current flow, it's only for backward compatibility.   //
					///////////////////////////////////////////////////////////////////////////////////////////////
					Logger.log(new Log().withTag(TAG).put("setAccountState", "REQUIRE_TRUSTLINE"));
					// Create trustline transaction with KIN
					blockchainSource.createTrustLine(new KinCallback<Void>() {
						@Override
						public void onResponse(Void response) {
							setAccountState(CREATION_COMPLETED);
						}

						@Override
						public void onFailure(KinEcosystemException exception) {
							instance.error = exception;
							setAccountState(ERROR);
						}
					});
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

	@Override
	public void switchAccount(final int accountIndex, @NonNull final KinCallback<Boolean> callback) {
		Logger.log(new Log().withTag(TAG).put("switchAccount", "start"));
		final String address = blockchainSource.getPublicAddress(accountIndex);
		updateWalletAddressProcess(address, accountIndex, callback);
	}

	private void updateWalletAddressProcess(final String address, final int accountIndex,
		final KinCallback<Boolean> callback) {
		blockchainSource.getMigrationInfo(address, new KinCallback<MigrationInfo>() {
			@Override
			public void onResponse(MigrationInfo migrationInfo) {
				boolean isRestorable = migrationInfo.isRestorable();
				Logger.log(new Log().withTag(TAG).put("switchAccount", "isRestorable = " + isRestorable));
				if (isRestorable) {
					startMigrationProcess(migrationInfo, address, accountIndex, callback);
				} else {
					onFailure(ErrorUtil.createWalletWasNotCreatedInThisAppException());
				}
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				Logger.log(new Log().priority(Log.ERROR).withTag(TAG).text("getMigrationInfo: onFailure"));
				deleteRestoredAccount(accountIndex);
				callback.onFailure(exception);
			}
		});
	}

	private void startMigrationProcess(MigrationInfo migrationInfo, final String address, final int accountIndex,
		final KinCallback<Boolean> callback) {
		blockchainSource.startMigrationProcess(migrationInfo, address, new MigrationProcessListener() {
			@Override
			public void onMigrationStart() {
			}

			@Override
			public void onMigrationEnd() {
				updateWalletAddress(address, accountIndex, callback);
			}

			@Override
			public void onMigrationError(BlockchainException error) {
				deleteRestoredAccount(accountIndex);
				callback.onFailure(error);
			}
		});
	}

	private void updateWalletAddress(String address, final int accountIndex, final KinCallback<Boolean> callback) {
		//update sign in data with new wallet address and update servers
		authRepository.updateWalletAddress(address, new KinCallback<Boolean>() {
			@Override
			public void onResponse(Boolean response) {
				//switch to the new KinAccount
				if (blockchainSource.updateActiveAccount(accountIndex)) {
					callback.onResponse(response);
				} else {
					deleteRestoredAccount(accountIndex);
					callback.onFailure(ErrorUtil.createAccountCannotLoadedException(accountIndex));
				}
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				deleteRestoredAccount(accountIndex);
				callback.onFailure(exception);
				Logger.log(new Log().withTag(TAG).put("switchAccount", "ended with failure"));
			}
		});
	}

	/**
	 * This method should be called in case the restore process is failed somewhere in the way. Because currently when
	 * restoring an account the restored account is added to kinClient(to the end of the account list if it wasn't there
	 * before) even before the restore process is finished successfully. That is why if the restore is failed we need to
	 * delete the restored account from the end of the account list(if it was added), if it was already there then we
	 * don't care about it because the active one will be the same as before.
	 */
	private void deleteRestoredAccount(int accountIndex) {
		try {
			Logger.log(new Log().withTag(TAG).put("deleteRestoredAccount", "account index = " + accountIndex));
			blockchainSource.deleteAccount(accountIndex);
		} catch (DeleteAccountException e) {
			e.printStackTrace();
			Logger.log(new Log().priority(Log.ERROR).withTag(TAG).put("deleteRestoredAccount", "error " + e));
		}
	}

	private IKinAccount getKinAccount() {
		return blockchainSource.getKinAccount();
	}

	private void removeAccountCreationRegistration() {
		if (accountCreationRegistration != null) {
			accountCreationRegistration.remove();
			accountCreationRegistration = null;
		}
	}

	private boolean isValidState(int currentState, int newState) {
		return newState == ERROR || currentState == ERROR || newState >= currentState
			//allow recreating an account in case of switching account after restore
			|| (currentState == CREATION_COMPLETED && newState == REQUIRE_CREATION);
	}

}
