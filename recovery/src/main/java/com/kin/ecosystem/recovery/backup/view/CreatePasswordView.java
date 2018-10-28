package com.kin.ecosystem.recovery.backup.view;

import com.kin.ecosystem.recovery.base.BaseView;

public interface CreatePasswordView extends BaseView {

	void setEnterPasswordIsCorrect(boolean isCorrect);

	void setConfirmPasswordIsCorrect(boolean isCorrect);

	void enableNextButton();

	void disableNextButton();

	void showBackupFailed();
}
