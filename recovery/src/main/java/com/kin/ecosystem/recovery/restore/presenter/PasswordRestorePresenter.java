package com.kin.ecosystem.recovery.restore.presenter;


import com.kin.ecosystem.recovery.base.BasePresenter;
import com.kin.ecosystem.recovery.restore.view.PasswordRestoreView;

public interface PasswordRestorePresenter extends BasePresenter<PasswordRestoreView> {

	void onPasswordChanged(String password);

	void restoreClicked(String password);

	void finishClicked();
}
