package com.kin.ecosystem.recovery;

public interface BackupAndRestoreCallback {

	void onSuccess();

	void onCancel();

	void onFailure(Throwable throwable);
}
