package com.kin.ecosystem.recovery;

import com.kin.ecosystem.recovery.exception.BackupAndRestoreException;

public interface BackupAndRestoreCallback {

	void onSuccess();

	void onCancel();

	void onFailure(BackupAndRestoreException exception);
}
