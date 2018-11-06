package com.kin.ecosystem.recovery.restore.presenter;


import com.kin.ecosystem.recovery.restore.view.RestoreCompletedView;

public class RestoreCompletedPresenterImpl extends BaseChildPresenterImpl<RestoreCompletedView> implements
	RestoreCompletedPresenter {

	private final int accountId;

	public RestoreCompletedPresenterImpl(int accountId) {
		this.accountId = accountId;
	}

	@Override
	public RestoreCompletedView getView() {
		return null;
	}

	@Override
	public void onBackClicked() {
		getParentPresenter().previousStep();
	}

	@Override
	public void finish() {
		getParentPresenter().nextStep(accountId);
	}
}
