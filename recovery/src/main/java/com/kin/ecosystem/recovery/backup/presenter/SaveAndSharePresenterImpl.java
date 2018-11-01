package com.kin.ecosystem.recovery.backup.presenter;

import static com.kin.ecosystem.recovery.backup.view.BackupNextStepListener.STEP_WELL_DONE;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.backup.view.BackupNextStepListener;
import com.kin.ecosystem.recovery.backup.view.SaveAndShareView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;

public class SaveAndSharePresenterImpl extends BasePresenterImpl<SaveAndShareView> implements SaveAndSharePresenter {

	private final BackupNextStepListener nextStepListener;
	private final QRBarcodeGenerator qrBarcodeGenerator;
	private final CallbackManager callbackManager;

	private Uri qrURI;


	public SaveAndSharePresenterImpl(@NonNull final CallbackManager callbackManager, BackupNextStepListener nextStepListener, QRBarcodeGenerator qrBarcodeGenerator,
		String key) {
		this.callbackManager = callbackManager;
		this.nextStepListener = nextStepListener;
		this.qrBarcodeGenerator = qrBarcodeGenerator;
		createQR(key);
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
			nextStepListener.setStep(STEP_WELL_DONE, null);
		}
	}

	@Override
	public void sendQREmailClicked() {
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
}
