package com.kin.ecosystem.recovery.backup.presenter;

import com.kin.ecosystem.recovery.backup.view.BackupNextStepListener;
import com.kin.ecosystem.recovery.backup.view.BackupView;
import com.kin.ecosystem.recovery.backup.view.CreatePasswordView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;

public class CreatePasswordPresenterImpl extends BasePresenterImpl<CreatePasswordView> implements CreatePasswordPresenter {

	private final BackupNextStepListener backupNextStepListener;

	public CreatePasswordPresenterImpl(BackupNextStepListener backupNextStepListener) {
		this.backupNextStepListener = backupNextStepListener;
	}

	@Override
	public void onBackClicked() {

	}
}
