package com.kin.ecosystem.recovery.restore.presenter;


import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.restore.view.RestoreView;

public class RestorePresenterImpl extends BasePresenterImpl<RestoreView> implements RestorePresenter {

	private static final int STEP_INITIAL = 0;
	private static final int STEP_UPLOAD = 1;
	private static final int STEP_ENTER_PASSWORD = 2;
	private static final int STEP_RESTORE_COMPLETED = 3;
	private static final int STEP_FINISH = 4;

	private int currentStep = 0;

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
				//CallbackManager callbackManager = new CallbackManager(new EventDispatcherImpl())
				getView().close();
				break;
		}
	}

	@Override
	public void previousStep() {
		switch (currentStep) {
			case STEP_UPLOAD:
				getView().close();
				break;
		}
		currentStep--;
	}

}
