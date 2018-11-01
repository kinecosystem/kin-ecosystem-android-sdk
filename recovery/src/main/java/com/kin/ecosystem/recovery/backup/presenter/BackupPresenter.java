package com.kin.ecosystem.recovery.backup.presenter;

import com.kin.ecosystem.recovery.backup.view.BackupNextStepListener;
import com.kin.ecosystem.recovery.backup.view.BackupView;
import com.kin.ecosystem.recovery.base.BasePresenter;

public interface BackupPresenter extends BasePresenter<BackupView>, BackupNextStepListener {

}
