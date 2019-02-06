package com.kin.ecosystem.recovery;

import static com.kin.ecosystem.recovery.exception.BackupAndRestoreException.RESTORE_SWITCH_ACCOUNT_FAILED;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.accountmanager.AccountManager;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.BackupWalletCompleted;
import com.kin.ecosystem.core.bi.events.GeneralEcosystemSdkError;
import com.kin.ecosystem.core.bi.events.RestoreWalletCompleted;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.settings.SettingsDataSource;
import com.kin.ecosystem.core.util.Validator;
import com.kin.ecosystem.recovery.exception.BackupAndRestoreException;
import com.kin.ecosystem.recovery.exception.BackupException;
import com.kin.ecosystem.recovery.exception.BackupRestoreErrorUtil;

public class BackupAndRestoreImpl implements BackupAndRestore {

	protected final BackupManager backupManager;
	private final AccountManager accountManager;
	private final EventLogger eventLogger;
	private final SettingsDataSource settingsDataSource;
	private final BlockchainSource blockchainSource;

	public BackupAndRestoreImpl(@NonNull final Activity activity, @NonNull AccountManager accountManager,
		@NonNull EventLogger eventLogger, @NonNull BlockchainSource blockchainSource,
		SettingsDataSource settingsDataSource) {
		this.blockchainSource = blockchainSource;
		this.backupManager = new BackupManager(activity, blockchainSource.getKeyStoreProvider());
		this.accountManager = accountManager;
		this.eventLogger = eventLogger;
		this.settingsDataSource = settingsDataSource;
	}

	@Override
	public void backupFlow() throws ClientException {
		if (blockchainSource.getKinAccount() != null) {
			backupManager.backupFlow();
		} else {
			throw new ClientException(ClientException.ACCOUNT_NOT_LOGGED_IN,
				"Account should be logged in before backup.", null);
		}
	}

	@Override
	public void restoreFlow() throws ClientException {
		if (blockchainSource.getKinAccount() != null) {
			backupManager.restoreFlow();
		} else {
			throw new ClientException(ClientException.ACCOUNT_NOT_LOGGED_IN,
				"Account should be logged in before restore.", null);
		}
	}

	@Override
	public void registerBackupCallback(@NonNull final BackupAndRestoreCallback backupCallback) {
		Validator.checkNotNull(backupCallback, "backupCallback");
		backupManager.registerBackupCallback(new BackupCallback() {

			@Override
			public void onSuccess() {
				try {
					final String publicAddress = blockchainSource.getPublicAddress();
					settingsDataSource.setIsBackedUp(publicAddress, true);
				} catch (ClientException | BlockchainException e) {
					eventLogger.send(GeneralEcosystemSdkError
						.create(
							"BackupAndRestoreImpl onSuccess blockchainSource.getPublicAddress() thrown an exception"));
				}
				eventLogger.send(BackupWalletCompleted.create());
				backupCallback.onSuccess();
			}

			@Override
			public void onCancel() {
				backupCallback.onCancel();
			}

			@Override
			public void onFailure(BackupException exception) {
				backupCallback.onFailure(BackupRestoreErrorUtil.get(exception));
			}
		});
	}

	private void switchAccount(int accountIndex, @NonNull final BackupAndRestoreCallback backupCallback) {
		accountManager.switchAccount(accountIndex, new KinCallback<Boolean>() {
			@Override
			public void onResponse(Boolean response) {
				eventLogger.send(RestoreWalletCompleted.create());
				backupCallback.onSuccess();
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				backupCallback.onFailure(new BackupAndRestoreException(RESTORE_SWITCH_ACCOUNT_FAILED,
					exception != null ? exception.getMessage() : "Switch account failed", exception));
			}
		});
	}

	@Override
	public void registerRestoreCallback(@NonNull final BackupAndRestoreCallback restoreCallback) {
		Validator.checkNotNull(restoreCallback, "restoreCallback");
		backupManager.registerRestoreCallback(new RestoreCallback() {
			@Override
			public void onSuccess(int index) {
				switchAccount(index, restoreCallback);
			}

			@Override
			public void onCancel() {
				restoreCallback.onCancel();
			}

			@Override
			public void onFailure(BackupException exception) {
				restoreCallback.onFailure(BackupRestoreErrorUtil.get(exception));
			}
		});
	}

	@Override
	public void release() {
		backupManager.release();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		backupManager.onActivityResult(requestCode, resultCode, data);
	}
}
