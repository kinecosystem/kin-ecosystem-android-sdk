package com.kin.ecosystem.recovery.restore.presenter;


import android.content.Intent;
import android.net.Uri;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator.QRFileHandlingException;
import com.kin.ecosystem.recovery.restore.presenter.FileSharingHelper.RequestFileResult;
import com.kin.ecosystem.recovery.restore.view.UploadQRView;
import com.kin.ecosystem.recovery.utils.Logger;

public class UploadQRPresenterImpl extends BaseChildPresenterImpl<UploadQRView> implements UploadQRPresenter {

	private final FileSharingHelper fileRequester;
	private final QRBarcodeGenerator qrBarcodeGenerator;

	public UploadQRPresenterImpl(FileSharingHelper fileRequester,
		QRBarcodeGenerator qrBarcodeGenerator) {
		this.fileRequester = fileRequester;
		this.qrBarcodeGenerator = qrBarcodeGenerator;
	}

	@Override
	public void uploadClicked() {
		getView().showConsentDialog();
	}

	@Override
	public void onConsent(String chooserTitle) {
		fileRequester.requestImageFile(chooserTitle);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		RequestFileResult requestFileResult = fileRequester.extractUriFromActivityResult(requestCode, resultCode, data);
		switch (requestFileResult.result) {
			case FileSharingHelper.REQUEST_RESULT_CANCELED:
				break;
			case FileSharingHelper.REQUEST_RESULT_FAILED:
				view.showErrorLoadingFileDialog();
				break;
			case FileSharingHelper.REQUEST_RESULT_OK:
				loadEncryptedKeyStore(requestFileResult.fileUri);
				break;
		}
	}

	private void loadEncryptedKeyStore(Uri fileUri) {
		try {
			String encryptedKeyStore = qrBarcodeGenerator.decodeQR(fileUri);
			getParentPresenter().nextStep(encryptedKeyStore);
		} catch (QRFileHandlingException e) {
			Logger.e("loadEncryptedKeyStore - loading file failed.", e);
			view.showErrorLoadingFileDialog();
		} catch (QRBarcodeGeneratorException e) {
			Logger.e("loadEncryptedKeyStore - decoding QR failed.", e);
			view.showErrorDecodingQRDialog();
		}
	}

	@Override
	public void onBackClicked() {
		getParentPresenter().previousStep();
	}
}
