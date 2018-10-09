package com.kin.ecosystem.backup;

public interface Callback {

	void onSuccess();

	void onFailure(Throwable exception);
}
