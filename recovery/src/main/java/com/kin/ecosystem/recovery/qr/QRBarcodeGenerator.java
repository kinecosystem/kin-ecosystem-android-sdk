package com.kin.ecosystem.recovery.qr;


import android.net.Uri;
import android.support.annotation.NonNull;

public interface QRBarcodeGenerator {


	@NonNull
	Uri generate(@NonNull String text) throws QRBarcodeGeneratorException;

	@NonNull
	String decodeQR(@NonNull Uri uri) throws QRBarcodeGeneratorException;

	class QRBarcodeGeneratorException extends Exception {

		QRBarcodeGeneratorException(String msg, Throwable throwable) {
			super(msg, throwable);
		}
	}

	class QRNotFoundInImageException extends QRBarcodeGeneratorException {

		QRNotFoundInImageException(String msg, Throwable throwable) {
			super(msg, throwable);
		}
	}

	class QRFileHandlingException extends QRBarcodeGeneratorException {

		QRFileHandlingException(String msg, Throwable throwable) {
			super(msg, throwable);
		}
	}
}
