package com.kin.ecosystem.recovery.restore.presenter;


import com.kin.ecosystem.recovery.BackupManager;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import com.kin.ecosystem.recovery.exception.BackupException;
import com.kin.ecosystem.recovery.restore.view.RestoreEnterPasswordView;
import com.kin.ecosystem.recovery.utils.Logger;

public class RestoreEnterPasswordPresenterImpl extends BaseChildPresenterImpl<RestoreEnterPasswordView> implements
	RestoreEnterPasswordPresenter {

	private final String keystoreData;

	public RestoreEnterPasswordPresenterImpl(String keystoreData) {
		this.keystoreData = keystoreData;
	}

	@Override
	public void onAttach(RestoreEnterPasswordView view) {
		super.onAttach(view);

		getView().openKeyboard();
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
		KeyStoreProvider keyStoreProvider = BackupManager.getKeyStoreProvider();
		try {
			int accountIndex = keyStoreProvider.importAccount(keystoreData, password);
			getParentPresenter().nextStep(accountIndex);
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
	public void onBackClicked() {
		getParentPresenter().previousStep();
	}
}
