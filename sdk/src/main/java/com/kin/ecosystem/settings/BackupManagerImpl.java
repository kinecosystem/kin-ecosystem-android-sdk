package com.kin.ecosystem.settings;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.kin.ecosystem.core.accountmanager.AccountManager;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.settings.SettingsDataSource;
import com.kin.ecosystem.recovery.BackupAndRestoreImpl;
import com.kin.ecosystem.recovery.BackupEvents;
import com.kin.ecosystem.recovery.RestoreEvents;

public final class BackupManagerImpl extends BackupAndRestoreImpl implements BackupManager {

	public BackupManagerImpl(@NonNull Activity activity, @NonNull AccountManager accountManager,@NonNull  EventLogger eventLogger, @NonNull
		BlockchainSource blockchainSource, @NonNull SettingsDataSource settingsDataSource) {
		super(activity, accountManager, eventLogger, blockchainSource, settingsDataSource);
	}

	@Override
	public void registerBackupEvents(@NonNull BackupEvents backupEvents) {
		backupManager.registerBackupEvents(backupEvents);
	}

	@Override
	public void registerRestoreEvents(@NonNull RestoreEvents restoreEvents) {
		backupManager.registerRestoreEvents(restoreEvents);
	}
}
