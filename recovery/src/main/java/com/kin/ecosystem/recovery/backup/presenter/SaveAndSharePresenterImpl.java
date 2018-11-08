package com.kin.ecosystem.recovery.backup.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.backup.view.BackupNavigator;
import com.kin.ecosystem.recovery.backup.view.SaveAndShareView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;

public class SaveAndSharePresenterImpl extends BasePresenterImpl<SaveAndShareView> implements SaveAndSharePresenter {

	private static final String IS_SEND_EMAIL_CLICKED = "is_send_email_clicked";
	private final BackupNavigator backupNavigator;
	private final QRBarcodeGenerator qrBarcodeGenerator;
	private final CallbackManager callbackManager;

	private Uri qrURI;
	private boolean isSendQREmailClicked;


	public SaveAndSharePresenterImpl(@NonNull final CallbackManager callbackManager,
		BackupNavigator backupNavigator,
		QRBarcodeGenerator qrBarcodeGenerator, String key, Bundle savedInstanceState) {
		this.callbackManager = callbackManager;
		this.backupNavigator = backupNavigator;
		this.qrBarcodeGenerator = qrBarcodeGenerator;
		this.isSendQREmailClicked = getIsSendQrEmailClicked(savedInstanceState);
		createQR(key);
	}

	private boolean getIsSendQrEmailClicked(Bundle savedInstanceState) {
		return savedInstanceState != null && savedInstanceState.getBoolean(IS_SEND_EMAIL_CLICKED);
	}

	private void createQR(String key) {
		try {
			this.qrURI = this.qrBarcodeGenerator.generate(key);
		} catch (QRBarcodeGeneratorException e) {
			couldNotLoadQRImage();
		}
	}

	@Override
	public void onAttach(SaveAndShareView view) {
		super.onAttach(view);
		setQRImage();
		if (isSendQREmailClicked && view != null) {
			view.showIHaveSavedCheckBox();
		}
		callbackManager.sendBackupEvents(EventDispatcherImpl.BACKUP_QR_CODE_PAGE_VIEWED);
	}

	private void setQRImage() {
		if (view != null) {
			view.setQRImage(qrURI);
		}
	}

	@Override
	public void onBackClicked() {

	}

	@Override
	public void iHaveSavedChecked(boolean isChecked) {
		if (isChecked) {
			backupNavigator.navigateToWellDonePage();
		}
	}

	@Override
	public void sendQREmailClicked() {
		isSendQREmailClicked = true;
		if (qrURI != null && view != null) {
			view.showSendIntent(qrURI);
			view.showIHaveSavedCheckBox();
		}
	}

	@Override
	public void couldNotLoadQRImage() {
		if (view != null) {
			view.showErrorTryAgainLater();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(IS_SEND_EMAIL_CLICKED, isSendQREmailClicked);
	}
}
