package com.kin.ecosystem.recovery.restore.view;


import com.kin.ecosystem.recovery.base.BaseView;

public interface RestoreEnterPasswordView extends BaseView {

	void enableDoneButton();

	void disableDoneButton();

	void decodeError();

	void invalidQrError();
}
