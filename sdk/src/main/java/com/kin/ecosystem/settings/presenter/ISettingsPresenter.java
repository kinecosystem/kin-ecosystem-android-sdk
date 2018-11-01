package com.kin.ecosystem.settings.presenter;

import android.content.Intent;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.settings.view.ISettingsView;

public interface ISettingsPresenter extends IBasePresenter<ISettingsView> {

	void backupClicked();

	void restoreClicked();

	void onActivityResult(int requestCode, int resultCode, Intent data);

	void backClicked();
}
