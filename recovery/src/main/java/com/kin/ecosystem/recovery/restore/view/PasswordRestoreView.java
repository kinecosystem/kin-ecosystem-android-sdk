package com.kin.ecosystem.recovery.restore.view;


import com.kin.ecosystem.recovery.base.BaseView;

public interface PasswordRestoreView extends BaseView {

	void openKeyboard();

	void enableDone();

	void revealRestoreSucceed();

	void decodeError();
}
