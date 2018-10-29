package com.kin.ecosystem.recovery.backup.presenter;

import com.kin.ecosystem.recovery.backup.view.SaveAndShareView;
import com.kin.ecosystem.recovery.base.BasePresenter;

public interface SaveAndSharePresenter extends BasePresenter<SaveAndShareView> {

	void iHaveSavedChecked(boolean isChecked);

	void sendQREmailClicked();

	void couldNotLoadQRImage();
}
