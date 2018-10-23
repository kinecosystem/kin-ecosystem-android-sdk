package com.kin.ecosystem.recovery.restore.view;


import com.kin.ecosystem.recovery.base.BaseView;

public interface RestoreView extends BaseView {

	void navigateToUpload();

	void navigateToEnterPassword(String keystoreData);

	void navigateToEnterPassword();

	void navigateToRestoreCompleted(Integer data);

	void navigateToRestoreCompleted();

	void close();

}
