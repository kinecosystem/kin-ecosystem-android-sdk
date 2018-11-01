package com.kin.ecosystem.recovery.backup.presenter;


import com.kin.ecosystem.recovery.backup.view.BackupNextStepListener;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.base.BaseView;

public class BackupInfoPresenterImpl extends BasePresenterImpl<BaseView> implements BackupInfoPresenter {

	private final BackupNextStepListener backupNextStepListener;

	public BackupInfoPresenterImpl(BackupNextStepListener backupNextStepListener) {
		this.backupNextStepListener = backupNextStepListener;
	}

	@Override
	public void onBackClicked() {
		backupNextStepListener.setStep(BackupNextStepListener.STEP_CLOSE, null);
	}

	@Override
	public void letsGoButtonClicked() {
		if (view != null) {
			backupNextStepListener.setStep(BackupNextStepListener.STEP_CREATE_PASSWORD, null);
		}
	}
}
