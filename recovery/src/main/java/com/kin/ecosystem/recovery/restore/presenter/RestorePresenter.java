package com.kin.ecosystem.recovery.restore.presenter;


import com.kin.ecosystem.recovery.base.BasePresenter;
import com.kin.ecosystem.recovery.restore.view.RestoreView;

public interface RestorePresenter extends BasePresenter<RestoreView> {

	void nextStep();

	void nextStep(Object data);

	void previousStep();
}
