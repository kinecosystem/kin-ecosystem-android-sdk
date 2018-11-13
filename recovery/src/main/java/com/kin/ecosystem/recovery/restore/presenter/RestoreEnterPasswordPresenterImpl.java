package com.kin.ecosystem.recovery.restore.presenter;


import static com.kin.ecosystem.recovery.events.RestoreEventCode.RESTORE_PASSWORD_DONE_TAPPED;
import static com.kin.ecosystem.recovery.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED;
import static com.kin.ecosystem.recovery.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_VIEWED;
import static com.kin.ecosystem.recovery.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_KEY;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.BackupManager;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.exception.BackupException;
import com.kin.ecosystem.recovery.restore.view.RestoreEnterPasswordView;
import com.kin.ecosystem.recovery.utils.Logger;

public class RestoreEnterPasswordPresenterImpl extends BaseChildPresenterImpl<RestoreEnterPasswordView> implements
	RestoreEnterPasswordPresenter {

	private final String keystoreData;
	private final CallbackManager callbackManager;

	public RestoreEnterPasswordPresenterImpl(@NonNull final CallbackManager callbackManager, String keystoreData) {
		this.callbackManager = callbackManager;
		this.keystoreData = keystoreData;
		this.callbackManager.sendRestoreEvents(RESTORE_PASSWORD_ENTRY_PAGE_VIEWED);
	}

	@Override
	public void onAttach(RestoreEnterPasswordView view) {
		super.onAttach(view);
	}

	@Override
	public void onPasswordChanged(String password) {
		if (password.isEmpty()) {
			getView().disableDoneButton();
		} else {
			getView().enableDoneButton();
		}
	}

	@Override
	public void restoreClicked(String password) {
		callbackManager.sendRestoreEvents(RESTORE_PASSWORD_DONE_TAPPED);
		KeyStoreProvider keyStoreProvider = BackupManager.getKeyStoreProvider();
		try {
			int accountIndex = keyStoreProvider.importAccount(keystoreData, password);
			getParentPresenter().navigateToRestoreCompletedPage(accountIndex);
		} catch (BackupException e) {
			Logger.e("RestoreEnterPasswordPresenterImpl - restore failed.", e);
			if (e.getCode() == BackupException.CODE_RESTORE_INVALID_KEYSTORE_FORMAT) {
				getView().invalidQrError();
			} else {
				getView().decodeError();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_ACCOUNT_KEY, keystoreData);
	}

	@Override
	public void onBackClicked() {
		callbackManager.sendRestoreEvents(RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED);
		getParentPresenter().previousStep();
	}
}
