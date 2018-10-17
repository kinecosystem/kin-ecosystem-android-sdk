package com.kin.ecosystem.settings.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.settings.view.ISettingsView;

public interface ISettingsPresenter extends IBasePresenter<ISettingsView> {

	void backupClicked();

	void restoreClicked();

	void onActivityResult(int requestCode, int resultCode, Intent data);

	void backClicked();
}
