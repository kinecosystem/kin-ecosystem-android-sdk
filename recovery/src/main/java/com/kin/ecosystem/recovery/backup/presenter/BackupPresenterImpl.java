package com.kin.ecosystem.recovery.backup.presenter;


import android.os.Bundle;
import android.support.annotation.Nullable;
import com.kin.ecosystem.recovery.backup.view.BackupView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;

public class BackupPresenterImpl extends BasePresenterImpl<BackupView> implements BackupPresenter {

	private @Step
	int step = STEP_START;
	private final CallbackManager callbackManager;
	private boolean isBackupSucceed = false;


	public BackupPresenterImpl(CallbackManager callbackManager) {
		this.callbackManager = callbackManager;
	}

	@Override
	public void onAttach(BackupView view) {
		super.onAttach(view);
		setStep(step, null);
	}

	@Override
	public void onBackClicked() {
		if (step == STEP_WELL_DONE) {
			setStep(STEP_CLOSE, null);
		} else {
			if (view != null) {
				if(!isBackupSucceed && step == STEP_CREATE_PASSWORD) {
					callbackManager.sendCancelledResult();
				}
				step--;
				view.onBackButtonClicked();
			}
		}
	}

	@Override
	public void setStep(@Step final int step, @Nullable Bundle data) {
		if (view != null) {
			this.step = step;
			switch (step) {
				case STEP_START:
					view.startBackupFlow();
					break;
				case STEP_CREATE_PASSWORD:
					view.moveToCreatePasswordPage();
					break;
				case STEP_SAVE_AND_SHARE:
					if (data != null) {
						final String key = data.getString(KEY_ACCOUNT_KEY, null);
						view.moveToSaveAndSharePage(key);
					}
					isBackupSucceed = true;
					callbackManager.sendBackupSuccessResult();
					break;
				case STEP_WELL_DONE:
					view.moveToWellDonePage();
					break;
				case STEP_CLOSE:
					view.close();
					break;
			}
		}
	}
}
