package com.kin.ecosystem.recovery.backup.presenter;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.recovery.backup.view.BackupView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;

public class BackupPresenterImpl extends BasePresenterImpl<BackupView> implements BackupPresenter {

	private static final String KEY_STEP = "kinrecovery_step";
	public static final String KEY_ACCOUNT_KEY = "kinrecovery_account_key";

	private @Step
	int step;
	private final CallbackManager callbackManager;
	private boolean isBackupSucceed = false;
	private final Bundle savedInstanceState;


	public BackupPresenterImpl(CallbackManager callbackManager, @Nullable final Bundle savedInstanceState) {
		this.callbackManager = callbackManager;
		this.savedInstanceState = savedInstanceState != null ? savedInstanceState : new Bundle();
		this.step = getStep();
	}

	private int getStep() {
		return savedInstanceState != null ? savedInstanceState.getInt(KEY_STEP, STEP_START) : STEP_START;
	}

	@Override
	public void onAttach(BackupView view) {
		super.onAttach(view);
		setStep(step, savedInstanceState);
	}

	@Override
	public void onBackClicked() {
		if (step == STEP_WELL_DONE) {
			setStep(STEP_CLOSE, null);
		} else {
			if (view != null) {
				if (!isBackupSucceed && step == STEP_CREATE_PASSWORD) {
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
						final String key = getAccountKey(data);
						view.moveToSaveAndSharePage(key);
						isBackupSucceed = true;
						callbackManager.sendBackupSuccessResult();
					} else {
						view.showError();
						view.close();
					}
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_STEP, step);
		outState.putString(KEY_ACCOUNT_KEY, getAccountKey(savedInstanceState));
	}

	@Override
	public void saveKeyData(String key) {
		this.savedInstanceState.putString(KEY_ACCOUNT_KEY, key);
	}

	private String getAccountKey(@NonNull final Bundle data) {
		return data.getString(KEY_ACCOUNT_KEY);
	}
}
