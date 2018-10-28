package com.kin.ecosystem.recovery.restore.presenter;


import android.content.Intent;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.restore.view.RestoreView;

public class RestorePresenterImpl extends BasePresenterImpl<RestoreView> implements RestorePresenter {

	private static final int STEP_INITIAL = 0;
	private static final int STEP_UPLOAD = 1;
	private static final int STEP_ENTER_PASSWORD = 2;
	private static final int STEP_RESTORE_COMPLETED = 3;
	private static final int STEP_FINISH = 4;

	private int currentStep = 0;

	private final CallbackManager callbackManager;

	public RestorePresenterImpl(CallbackManager callbackManager) {
		this.callbackManager = callbackManager;
	}

	@Override
	public void onAttach(RestoreView view) {
		super.onAttach(view);
		currentStep = STEP_INITIAL;
		nextStep();
	}

	@Override
	public void onBackClicked() {
		previousStep();
	}

	@Override
	public void nextStep() {
		nextStep(null);
	}

	@Override
	public void nextStep(Object data) {
		currentStep++;
		switch (currentStep) {
			case STEP_UPLOAD:
				getView().navigateToUpload();
				break;
			case STEP_ENTER_PASSWORD:
				getView().navigateToEnterPassword((String) data);
				break;
			case STEP_RESTORE_COMPLETED:
				getView().navigateToRestoreCompleted((Integer) data);
				break;
			case STEP_FINISH:
				callbackManager.sendRestoreSuccessResult((Integer) data);
				getView().close();
				break;
		}
	}

	@Override
	public void previousStep() {
		switch (currentStep) {
			case STEP_UPLOAD:
				callbackManager.sendRestoreCancelledResult();
				getView().close();
				break;
			case STEP_ENTER_PASSWORD:
				getView().navigateBack();
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

}
