package com.kin.ecosystem.recovery;

import com.kin.ecosystem.recovery.exception.BackupException;

public interface BackupCallback {

	void onSuccess();

	void onCancel();

	void onFailure(BackupException exception);
}
