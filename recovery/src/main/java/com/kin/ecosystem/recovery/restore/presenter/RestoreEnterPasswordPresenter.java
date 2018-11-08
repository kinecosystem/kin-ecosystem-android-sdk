package com.kin.ecosystem.recovery.restore.presenter;


import android.os.Bundle;
import com.kin.ecosystem.recovery.restore.view.RestoreEnterPasswordView;

public interface RestoreEnterPasswordPresenter extends BaseChildPresenter<RestoreEnterPasswordView> {

	void onPasswordChanged(String password);

	void restoreClicked(String password);

	void onSaveInstanceState(Bundle outState);
}
