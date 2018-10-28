package com.kin.ecosystem.recovery.backup.view;

import com.kin.ecosystem.recovery.base.BaseView;

public interface BackupView extends BaseView {

	void startBackupFlow();

	void moveToSetPasswordPage();

	void moveToSaveAndSharePage(String key);

	void backButtonClicked();

	void moveToWellDonePage();

	void close();
}
