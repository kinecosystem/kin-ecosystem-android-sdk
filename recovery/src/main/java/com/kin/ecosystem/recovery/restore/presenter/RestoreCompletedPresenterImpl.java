package com.kin.ecosystem.recovery.restore.presenter;


import static com.kin.ecosystem.recovery.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_INDEX;

import android.os.Bundle;
import com.kin.ecosystem.recovery.restore.view.RestoreCompletedView;

public class RestoreCompletedPresenterImpl extends BaseChildPresenterImpl<RestoreCompletedView> implements
	RestoreCompletedPresenter {

	private final int accountIndex;

	public RestoreCompletedPresenterImpl(int accountIndex) {
		this.accountIndex = accountIndex;
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
	public void close() {
		getParentPresenter().closeFlow(accountIndex);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_ACCOUNT_INDEX, accountIndex);
	}
}
