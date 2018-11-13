package com.kin.ecosystem.recovery.backup.presenter;

import android.os.Bundle;
import com.kin.ecosystem.recovery.backup.view.BackupNavigator;
import com.kin.ecosystem.recovery.backup.view.BackupView;
import com.kin.ecosystem.recovery.base.BasePresenter;

public interface BackupPresenter extends BasePresenter<BackupView>, BackupNavigator {

	void onSaveInstanceState(Bundle outState);

	void setAccountKey(String key);
}
