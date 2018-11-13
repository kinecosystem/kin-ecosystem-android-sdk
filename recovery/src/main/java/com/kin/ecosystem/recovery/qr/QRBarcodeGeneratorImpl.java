package com.kin.ecosystem.recovery.qr;


import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.kin.ecosystem.recovery.utils.Logger;
import java.io.IOException;
import java.util.Collections;

public class QRBarcodeGeneratorImpl implements QRBarcodeGenerator {

	private static final int QR_PIXELS = 600;
	private final QRFileUriHandler fileUriHandler;

	public QRBarcodeGeneratorImpl(QRFileUriHandler fileUriHandler) {
		this.fileUriHandler = fileUriHandler;
	}

	@NonNull
	@Override
	public Uri generate(@NonNull String text) throws QRBarcodeGeneratorException {
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, QR_PIXELS, QR_PIXELS);
			Bitmap bitmap = bitMatrixToBitmap(bitMatrix);
			return fileUriHandler.saveFile(bitmap);
		} catch (WriterException e) {
			Logger.e("decodeQR failed. ", e);
			throw new QRBarcodeGeneratorException("Cannot generate a QR, caused by : " + e.getMessage(), e);
		} catch (IOException e) {
			Logger.e("decodeQR failed. ", e);
			throw new QRFileHandlingException("Cannot load QR file, caused by :" + e.getMessage(), e);
		}
	}

	private Bitmap bitMatrixToBitmap(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	@NonNull
	@Override
	public String decodeQR(@NonNull Uri uri) throws QRBarcodeGeneratorException {
		try {
			Bitmap bitmap = fileUriHandler.loadFile(uri);
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

			RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
			BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

			MultiFormatReader reader = new MultiFormatReader();
			reader.setHints(Collections.singletonMap(DecodeHintType.POSSIBLE_FORMATS,
				Collections.singleton(BarcodeFormat.QR_CODE)));
			Result result = reader.decode(binaryBitmap);
			return result.getText();
		} catch (IOException e) {
			Logger.e("decodeQR failed. ", e);
			throw new QRFileHandlingException("Cannot load QR file, caused by :" + e.getMessage(), e);
		} catch (NotFoundException e) {
			Logger.e("decodeQR failed. ", e);
			throw new QRNotFoundInImageException("Cannot find a QR code in given image.", e);
		}
	}
}
