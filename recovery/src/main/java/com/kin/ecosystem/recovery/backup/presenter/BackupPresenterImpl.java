package com.kin.ecosystem.recovery.backup.presenter;


import com.kin.ecosystem.recovery.backup.view.BackupView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;

public class BackupPresenterImpl extends BasePresenterImpl<BackupView> implements BackupPresenter {


	@Override
	public void onAttach(BackupView view) {
		super.onAttach(view);
		setStep(STEP_START);
	}

	@Override
	public void onBackClicked() {
		if (view != null) {
			view.backButtonClicked();
		}
	}

	@Override
	public void setStep(@Step final int step) {
		if (view != null) {
			switch (step) {
				case STEP_START:
					view.startBackupFlow();
					break;
				case STEP_FIRST:
					view.moveToSetPasswordPage();
					break;
				case STEP_SECOND:
					view.moveToSaveAndSharePage();
					break;
				case STEP_CLOSE:
					view.close();
					break;
			}
		}
	}
}
