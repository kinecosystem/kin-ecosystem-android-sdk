package com.kin.ecosystem.backup.restore.presenter;


import android.content.Intent;
import android.net.Uri;
import com.kin.ecosystem.backup.base.BasePresenterImpl;
import com.kin.ecosystem.backup.qr.QRBarcodeGenerator;
import com.kin.ecosystem.backup.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;
import com.kin.ecosystem.backup.qr.QRBarcodeGenerator.QRFileHandlingException;
import com.kin.ecosystem.backup.restore.presenter.FileSharingHelper.RequestFileResult;
import com.kin.ecosystem.backup.restore.view.UploadQRView;
import com.kin.ecosystem.backup.utils.Logger;

public class UploadQRPresenterImpl extends BasePresenterImpl<UploadQRView> implements UploadQRPresenter {

	private final RestorePresenter parentPresenter;
	private final FileSharingHelper fileRequester;
	private final QRBarcodeGenerator qrBarcodeGenerator;

	public UploadQRPresenterImpl(RestorePresenter restorePresenter, FileSharingHelper fileRequester,
		QRBarcodeGenerator qrBarcodeGenerator) {
		this.parentPresenter = restorePresenter;
		this.fileRequester = fileRequester;
		this.qrBarcodeGenerator = qrBarcodeGenerator;
	}

	@Override
	public void uploadClicked() {
		fileRequester.requestImageFile();
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
		parentPresenter.previousStep();
	}
}
