package com.kin.ecosystem.recovery.backup.presenter;


import static com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED;
import static com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_VIEWED;

import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import com.kin.ecosystem.recovery.backup.view.BackupNavigator;
import com.kin.ecosystem.recovery.backup.view.CreatePasswordView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.exception.BackupException;

public class CreatePasswordPresenterImpl extends BasePresenterImpl<CreatePasswordView> implements
	CreatePasswordPresenter {

	private final BackupNavigator backupNavigator;
	private final KeyStoreProvider keyStoreProvider;
	private final CallbackManager callbackManager;

	private boolean isPasswordRulesOK = false;
	private boolean isPasswordsMatches = false;
	private boolean isIUnderstandChecked = false;

	public CreatePasswordPresenterImpl(@NonNull final CallbackManager callbackManager,
		@NonNull final BackupNavigator backupNavigator, @NonNull final KeyStoreProvider keyStoreProvider) {
		this.backupNavigator = backupNavigator;
		this.keyStoreProvider = keyStoreProvider;
		this.callbackManager = callbackManager;
		this.callbackManager.sendBackupEvent(BACKUP_CREATE_PASSWORD_PAGE_VIEWED);
	}

	@Override
	public void onBackClicked() {
		backupNavigator.closeFlow();
	}

	@Override
	public void enterPasswordChanged(String password, String confirmPassword) {
		if (keyStoreProvider.validatePassword(password)) {
			isPasswordRulesOK = true;
			if (view != null) {
				view.setEnterPasswordIsCorrect(true);
			}
			checkConfirmPassword(password, confirmPassword);
		} else {
			isPasswordRulesOK = false;
			if (password.length() == 0) {
				if (view != null) {
					view.resetEnterPasswordField();
					view.resetConfirmPasswordField();
				}
			} else {
				if (view != null) {
					view.setEnterPasswordIsCorrect(false);
				}
				checkConfirmPassword(password, confirmPassword);
			}
		}
		checkAllCompleted();
	}

	private void checkConfirmPassword(String password, String confirmPassword) {
		if (password.length() > 0 && confirmPassword.length() > 0) {
			if (password.equals(confirmPassword)) {
				isPasswordsMatches = true;
				if (view != null) {
					view.setConfirmPasswordIsCorrect(true);
					view.closeKeyboard();
				}
			} else {
				isPasswordsMatches = false;
				if (view != null) {
					view.setConfirmPasswordIsCorrect(false);
				}
			}
		} else {
			if (view != null) {
				view.resetConfirmPasswordField();
			}
		}
	}

	@Override
	public void confirmPasswordChanged(String mainPassword, String confirmPassword) {
		checkConfirmPassword(mainPassword, confirmPassword);
		checkAllCompleted();
	}

	@Override
	public void iUnderstandChecked(boolean isChecked) {
		isIUnderstandChecked = isChecked;
		checkAllCompleted();
	}

	@Override
	public void nextButtonClicked(String password) {
		callbackManager.sendBackupEvent(BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED);
		exportAccount(password);
	}

	private void exportAccount(String password) {
		try {
			final String accountKey = keyStoreProvider.exportAccount(password);
			backupNavigator.navigateToSaveAndSharePage(accountKey);
		} catch (BackupException e) {
			if (view != null) {
				view.showBackupFailed();
			}
		}
	}

	@Override
	public void onRetryClicked(String password) {
		exportAccount(password);
	}

	private void checkAllCompleted() {
		if (isPasswordRulesOK && isPasswordsMatches && isIUnderstandChecked) {
			enableNextButton();
		} else {
			disableNextButton();
		}
	}

	private void disableNextButton() {
		if (view != null) {
			view.disableNextButton();
		}
	}

	private void enableNextButton() {
		if (view != null) {
			view.enableNextButton();
		}
	}
}
