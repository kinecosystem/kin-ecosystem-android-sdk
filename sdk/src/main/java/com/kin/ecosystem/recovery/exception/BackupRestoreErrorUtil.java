package com.kin.ecosystem.recovery.exception;

import android.support.annotation.Nullable;

public class BackupRestoreErrorUtil {

	public static BackupAndRestoreException get(@Nullable BackupException e) {
		final BackupAndRestoreException exception;
		if(e != null) {
			switch (e.getCode()) {
				case BackupException.CODE_BACKUP_FAILED:
					exception = new BackupAndRestoreException(BackupAndRestoreException.BACKUP_FAILED, e.getMessage(), e);
					break;
				case BackupException.CODE_RESTORE_FAILED:
					exception = new BackupAndRestoreException(BackupAndRestoreException.RESTORE_FAILED, e.getMessage(), e);
					break;
				case BackupException.CODE_RESTORE_INVALID_KEYSTORE_FORMAT:
					exception = new BackupAndRestoreException(BackupAndRestoreException.RESTORE_INVALID_KEYSTORE_FORMAT, e.getMessage(), e);
					break;
				case BackupException.CODE_UNEXPECTED:
				default:
					exception = new BackupAndRestoreException(BackupAndRestoreException.UNKNOWN, e.getMessage(), e);
			}
		} else {
			exception = new BackupAndRestoreException(BackupAndRestoreException.UNKNOWN, "Unknown error occurred", null);
		}
		return exception;
	}
}
