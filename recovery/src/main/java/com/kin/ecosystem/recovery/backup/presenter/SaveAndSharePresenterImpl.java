package com.kin.ecosystem.recovery.backup.presenter;

import static com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_QR_CODE_PAGE_VIEWED;
import static com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_QR_PAGE_QR_SAVED_TAPPED;
import static com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_QR_PAGE_SEND_QR_TAPPED;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.backup.view.BackupNavigator;
import com.kin.ecosystem.recovery.backup.view.SaveAndShareView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;

public class SaveAndSharePresenterImpl extends BasePresenterImpl<SaveAndShareView> implements SaveAndSharePresenter {

	static final String IS_SEND_EMAIL_CLICKED = "is_send_email_clicked";
	static final String IS_SAVED_QR_CHECKED = "is_saved_qr_checked";
	private final BackupNavigator backupNavigator;
	private final QRBarcodeGenerator qrBarcodeGenerator;
	private final CallbackManager callbackManager;

	private Uri qrURI;
	private boolean isSendQREmailClicked;
	private boolean isSavedQRChecked;
	private boolean couldNotGenerateQR = false;

	public SaveAndSharePresenterImpl(@NonNull final CallbackManager callbackManager,
		BackupNavigator backupNavigator,
		QRBarcodeGenerator qrBarcodeGenerator, String key, Bundle savedInstanceState) {
		this.backupNavigator = backupNavigator;
		this.qrBarcodeGenerator = qrBarcodeGenerator;
		this.callbackManager = callbackManager;
		this.isSendQREmailClicked = getIsSendQrEmailClicked(savedInstanceState);
		this.isSavedQRChecked = getIsSavedQRChecked(savedInstanceState);
		this.callbackManager.sendBackupEvent(BACKUP_QR_CODE_PAGE_VIEWED);
		createQR(key);
	}

	private boolean getIsSendQrEmailClicked(Bundle savedInstanceState) {
		return savedInstanceState != null && savedInstanceState.getBoolean(IS_SEND_EMAIL_CLICKED);
	}

	private boolean getIsSavedQRChecked(Bundle savedInstanceState) {
		return savedInstanceState != null && savedInstanceState.getBoolean(IS_SAVED_QR_CHECKED);
	}

	private void createQR(String key) {
		try {
			this.qrURI = this.qrBarcodeGenerator.generate(key);
		} catch (QRBarcodeGeneratorException e) {
			couldNotGenerateQR = true;
		}
	}

	@Override
	public void onAttach(SaveAndShareView view) {
		super.onAttach(view);
		if (couldNotGenerateQR) {
			couldNotLoadQRImage();
		} else {
			setQRImage();
		}
		if (isSendQREmailClicked && view != null) {
			view.showIHaveSavedQRState();
		}
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
		isSavedQRChecked = isChecked;
		if (isChecked) {
			callbackManager.sendBackupEvent(BACKUP_QR_PAGE_QR_SAVED_TAPPED);
		}
		view.setActionButtonEnabled(isChecked);
	}

	@Override
	public void actionButtonClicked() {
		if (!isSendQREmailClicked) {
			isSendQREmailClicked = true;
			callbackManager.sendBackupEvent(BACKUP_QR_PAGE_SEND_QR_TAPPED);
			if (qrURI != null && view != null) {
				view.showSendIntent(qrURI);
				view.showIHaveSavedQRState();
			}
		} else {
			if (isSavedQRChecked) {
				backupNavigator.navigateToWellDonePage();
			}
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
		outState.putBoolean(IS_SAVED_QR_CHECKED, isSavedQRChecked);
	}
}
