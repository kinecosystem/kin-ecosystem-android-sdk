package com.kin.ecosystem.recovery.restore.presenter;


import android.content.Intent;
import android.os.Bundle;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.restore.view.RestoreView;

public class RestorePresenterImpl extends BasePresenterImpl<RestoreView> implements RestorePresenter {

	private static final int STEP_UPLOAD = 0;
	static final int STEP_ENTER_PASSWORD = 1;
	static final int STEP_RESTORE_COMPLETED = 2;
	static final int STEP_FINISH = 3;

	private static final String KEY_RESTORE_STEP = "kinrecovery_restore_step";
	public static final String KEY_ACCOUNT_KEY = "kinrecovery_restore_account_key";
	public static final String KEY_ACCOUNT_INDEX = "kinrecovery_restore_account_index";

	private int currentStep;
	private String accountKey;
	private int accountIndex;

	private final CallbackManager callbackManager;

	public RestorePresenterImpl(CallbackManager callbackManager, Bundle saveInstanceState) {
		this.callbackManager = callbackManager;
		this.currentStep = getStep(saveInstanceState);
		this.accountKey = getAccountKey(saveInstanceState);
		this.accountIndex = getAccountIndex(saveInstanceState);
	}


	@Override
	public void onAttach(RestoreView view) {
		super.onAttach(view);
		switchToStep(currentStep);
	}

	private int getStep(Bundle saveInstanceState) {
		return saveInstanceState != null ? saveInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD) : STEP_UPLOAD;
	}

	private String getAccountKey(Bundle saveInstanceState) {
		return saveInstanceState != null ? saveInstanceState.getString(KEY_ACCOUNT_KEY) : null;
	}

	private int getAccountIndex(Bundle saveInstanceState) {
		return saveInstanceState != null ? saveInstanceState.getInt(KEY_ACCOUNT_INDEX) : -1;
	}

	@Override
	public void onBackClicked() {
		previousStep();
	}

	private void switchToStep(int step) {
		currentStep = step;
		switch (step) {
			case STEP_UPLOAD:
				getView().navigateToUpload();
				break;
			case STEP_ENTER_PASSWORD:
				if (accountKey != null) {
					getView().navigateToEnterPassword(accountKey);
				} else {
					getView().showError();
				}
				break;
			case STEP_RESTORE_COMPLETED:
				getView().closeKeyboard();
				if (accountIndex != -1) {
					getView().navigateToRestoreCompleted(accountIndex);
				} else {
					getView().showError();
				}
				break;
			case STEP_FINISH:
				if (accountIndex != -1) {
					callbackManager.sendRestoreSuccessResult(accountIndex);
				} else {
					getView().showError();
				}
				getView().close();
				break;
		}
	}

	@Override
	public void navigateToEnterPasswordPage(final String accountKey) {
		this.accountKey = accountKey;
		switchToStep(STEP_ENTER_PASSWORD);
	}

	@Override
	public void navigateToRestoreCompletedPage(final int accountIndex) {
		this.accountIndex = accountIndex;
		switchToStep(STEP_RESTORE_COMPLETED);
	}

	@Override
	public void closeFlow(final int accountIndex) {
		this.accountIndex = accountIndex;
		switchToStep(STEP_FINISH);
	}


	@Override
	public void previousStep() {
		switch (currentStep) {
			case STEP_UPLOAD:
				callbackManager.sendCancelledResult();
				getView().close();
				break;
			case STEP_ENTER_PASSWORD:
				getView().navigateBack();
				getView().closeKeyboard();
				break;
			case STEP_RESTORE_COMPLETED:
				getView().navigateBack();
				break;
			case STEP_FINISH:
				getView().navigateBack();
				break;
		}
		currentStep--;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_RESTORE_STEP, currentStep);
		outState.putString(KEY_ACCOUNT_KEY, accountKey);
		outState.putInt(KEY_ACCOUNT_INDEX, accountIndex);
	}

}
