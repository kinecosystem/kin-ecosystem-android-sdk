package com.kin.ecosystem.recovery;

public interface BackupCallback {

	void onSuccess();

	void onCancel();

	void onFailure(Throwable throwable);
}
