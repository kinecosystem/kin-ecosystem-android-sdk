package com.kin.ecosystem.recovery.backup.presenter;


import static com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_WELCOME_PAGE_START_TAPPED;
import static com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_WELCOME_PAGE_VIEWED;

import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.backup.view.BackupNavigator;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.base.BaseView;
import com.kin.ecosystem.recovery.events.CallbackManager;

public class BackupInfoPresenterImpl extends BasePresenterImpl<BaseView> implements BackupInfoPresenter {

	private final BackupNavigator backupNavigator;
	private final CallbackManager callbackManager;

	public BackupInfoPresenterImpl(@NonNull CallbackManager callbackManager,
		BackupNavigator backupNavigator) {
		this.backupNavigator = backupNavigator;
		this.callbackManager = callbackManager;
		this.callbackManager.sendBackupEvent(BACKUP_WELCOME_PAGE_VIEWED);
		this.callbackManager.setCancelledResult(); // make sure cancel will be called if nothing happened.
	}

	@Override
	public void onBackClicked() {
		backupNavigator.closeFlow();
	}

	@Override
	public void letsGoButtonClicked() {
		callbackManager.sendBackupEvent(BACKUP_WELCOME_PAGE_START_TAPPED);
		if (view != null) {
			backupNavigator.navigateToCreatePasswordPage();
		}
	}
}
