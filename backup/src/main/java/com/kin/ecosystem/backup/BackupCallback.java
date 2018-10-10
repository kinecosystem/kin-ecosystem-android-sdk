package com.kin.ecosystem.backup;

public interface BackupCallback {

	void onSuccess();

	void onCancel();

	void onFailure(Throwable throwable);
}
