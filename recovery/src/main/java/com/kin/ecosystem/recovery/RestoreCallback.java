package com.kin.ecosystem.recovery;

import com.kin.ecosystem.recovery.exception.BackupException;

public interface RestoreCallback {

	void onSuccess(int index);

	void onCancel();

	void onFailure(BackupException throwable);
}
