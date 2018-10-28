package com.kin.ecosystem.recovery.backup.presenter;

import static com.kin.ecosystem.recovery.backup.view.BackupNextStepListener.STEP_WELL_DONE;

import android.net.Uri;
import com.kin.ecosystem.recovery.backup.view.BackupNextStepListener;
import com.kin.ecosystem.recovery.backup.view.SaveAndShareView;
import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;

public class SaveAndSharePresenterImpl extends BasePresenterImpl<SaveAndShareView> implements SaveAndSharePresenter {

	private final BackupNextStepListener nextStepListener;
	private final QRBarcodeGenerator qrBarcodeGenerator;

	private Uri qrURI;


	public SaveAndSharePresenterImpl(BackupNextStepListener nextStepListener, QRBarcodeGenerator qrBarcodeGenerator,
		String key) {
		this.nextStepListener = nextStepListener;
		this.qrBarcodeGenerator = qrBarcodeGenerator;
		createQR(key);
	}

	private void createQR(String key) {
		try {
			this.qrURI = this.qrBarcodeGenerator.generate(key);
		} catch (QRBarcodeGeneratorException e) {
			//TODO could not generate the QR
		}
	}

	@Override
	public void onAttach(SaveAndShareView view) {
		super.onAttach(view);
		setQRImage();
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
		}
	}
}
