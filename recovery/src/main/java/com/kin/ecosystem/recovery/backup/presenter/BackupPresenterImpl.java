package com.kin.ecosystem.recovery.backup.presenter;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.recovery.backup.view.BackupView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;

public class BackupPresenterImpl extends BasePresenterImpl<BackupView> implements BackupPresenter {

	private static final String KEY_STEP = "kinrecovery_backup_step";
	public static final String KEY_ACCOUNT_KEY = "kinrecovery_backup_account_key";

	private @Step
	int step;
	private final CallbackManager callbackManager;
	private boolean isBackupSucceed = false;
	private String accountKey;


	public BackupPresenterImpl(CallbackManager callbackManager, @Nullable final Bundle savedInstanceState) {
		this.callbackManager = callbackManager;
		this.step = getStep(savedInstanceState);
		this.accountKey = getAccountKey(savedInstanceState);
	}

	private int getStep(Bundle savedInstanceState) {
		return savedInstanceState != null ? savedInstanceState.getInt(KEY_STEP, STEP_START) : STEP_START;
	}

	private String getAccountKey(Bundle savedInstanceState) {
		return savedInstanceState != null ? savedInstanceState.getString(KEY_ACCOUNT_KEY) : null;
	}

	@Override
	public void onAttach(BackupView view) {
		super.onAttach(view);
		switchToStep(step);
	}

	@Override
	public void onBackClicked() {
		if (step == STEP_WELL_DONE) {
			switchToStep(STEP_CLOSE);
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


	private void switchToStep(@Step final int step) {
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
					if (accountKey != null) {
						view.moveToSaveAndSharePage(accountKey);
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
	public void navigateToCreatePasswordPage() {
		switchToStep(STEP_CREATE_PASSWORD);
	}

	@Override
	public void navigateToSaveAndSharePage(@NonNull String accountKey) {
		this.accountKey = accountKey;
		switchToStep(STEP_SAVE_AND_SHARE);
	}

	@Override
	public void navigateToWellDonePage() {
		switchToStep(STEP_WELL_DONE);
	}

	@Override
	public void closeFlow() {
		switchToStep(STEP_CLOSE);
	}

	@Override
	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_STEP, step);
		outState.putString(KEY_ACCOUNT_KEY, accountKey);
	}
}
