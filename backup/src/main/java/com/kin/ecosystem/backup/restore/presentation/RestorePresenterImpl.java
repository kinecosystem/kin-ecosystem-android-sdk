package com.kin.ecosystem.backup.restore.presentation;


import com.kin.ecosystem.backup.base.BasePresenterImpl;
import com.kin.ecosystem.backup.restore.view.RestoreView;

public class RestorePresenterImpl extends BasePresenterImpl<RestoreView> implements RestorePresenter {

	private static final int STEP_UPLOAD = 1;
	private static final int STEP_LOAD_FILE = 1;

	private int currentStep = 0;

	@Override
	public void onAttach(RestoreView view) {
		super.onAttach(view);
		currentStep = STEP_UPLOAD;
		navigateToUpload();
	}

	@Override
	public void onBackClicked() {
		previousStep();
	}

	@Override
	public void nextStep() {
		switch (currentStep) {
			case STEP_UPLOAD:
				loadQRFile();
				break;
		}
		currentStep++;
	}

	private void loadQRFile() {

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

	private void navigateToUpload() {
		getView().navigateToUpload();
	}
}
