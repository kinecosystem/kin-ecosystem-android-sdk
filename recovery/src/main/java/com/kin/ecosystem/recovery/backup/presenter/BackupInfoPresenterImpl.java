package com.kin.ecosystem.recovery.backup.presenter;


import static com.kin.ecosystem.recovery.events.EventDispatcherImpl.BACKUP_WELCOME_PAGE_VIEWED;

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
		this.callbackManager = callbackManager;
		this.backupNavigator = backupNavigator;
	}

	@Override
	public void onAttach(BaseView view) {
		super.onAttach(view);
		callbackManager.sendBackupEvents(BACKUP_WELCOME_PAGE_VIEWED);
	}

	@Override
	public void onBackClicked() {
		backupNavigator.closeFlow();
	}

	@Override
	public void letsGoButtonClicked() {
		if (view != null) {
			backupNavigator.navigateToCreatePasswordPage();
		}
	}
}
