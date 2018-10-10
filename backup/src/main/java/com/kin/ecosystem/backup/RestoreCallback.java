package com.kin.ecosystem.backup;

public interface RestoreCallback {

	void onSuccess(int index);

	void onCancel();

	void onFailure(Throwable throwable);
}
