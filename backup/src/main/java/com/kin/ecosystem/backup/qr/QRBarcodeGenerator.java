package com.kin.ecosystem.backup.qr;


import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

public interface QRBarcodeGenerator {


	@NonNull
	Uri generate(@NonNull String text) throws QRBarcodeGeneratorException;

	@NonNull
	String decodeQR(@NonNull Bitmap bitmap) throws QRBarcodeGeneratorException;

	class QRBarcodeGeneratorException extends Exception {

		public QRBarcodeGeneratorException(String msg, Throwable throwable) {
			super(msg, throwable);
		}
	}
}
