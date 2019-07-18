package com.kin.ecosystem.recovery.backup.presenter;

import android.os.Bundle;
import com.kin.ecosystem.recovery.backup.view.SaveAndShareView;
import com.kin.ecosystem.recovery.base.BasePresenter;

public interface SaveAndSharePresenter extends BasePresenter<SaveAndShareView> {

	void iHaveSavedChecked(boolean isChecked);

	void actionButtonClicked();

	void couldNotLoadQRImage();

	void onSaveInstanceState(Bundle outState);
}
