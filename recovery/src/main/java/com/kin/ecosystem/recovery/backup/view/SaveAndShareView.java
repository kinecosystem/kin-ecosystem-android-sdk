package com.kin.ecosystem.recovery.backup.view;

import android.net.Uri;
import com.kin.ecosystem.recovery.base.BaseView;

public interface SaveAndShareView extends BaseView {

	void setQRImage(Uri qrURI);

	void showSendIntent(Uri qrURI);

	void showIHaveSavedQRState();

	void updateDoneState(boolean isEnabled);

	void showErrorTryAgainLater();
}
