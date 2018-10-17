package com.kin.ecosystem.backup.restore.presentation;


import com.kin.ecosystem.backup.base.BasePresenter;
import com.kin.ecosystem.backup.restore.view.RestoreView;

public interface RestorePresenter extends BasePresenter<RestoreView> {

	void nextStep();

	void previousStep();
}
