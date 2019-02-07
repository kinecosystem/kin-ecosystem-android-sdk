package com.kin.ecosystem.settings;

import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.BackupAndRestore;
import com.kin.ecosystem.recovery.BackupEvents;
import com.kin.ecosystem.recovery.RestoreEvents;

public interface BackupManager extends BackupAndRestore {

	void registerBackupEvents(@NonNull final BackupEvents backupEvents);

	void registerRestoreEvents(@NonNull final RestoreEvents restoreEvents);
}
