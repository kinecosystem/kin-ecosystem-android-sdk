package com.kin.ecosystem.recovery;

public interface RestoreCallback {

	void onSuccess(int index);

	void onCancel();

	void onFailure(Throwable throwable);
}
