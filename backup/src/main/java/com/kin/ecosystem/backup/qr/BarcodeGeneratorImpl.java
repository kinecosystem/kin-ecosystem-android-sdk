package com.kin.ecosystem.backup.qr;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class BarcodeGeneratorImpl implements BarcodeGenerator {

	private final Context context;

	BarcodeGeneratorImpl(Context context) {
		this.context = context;
	}

	public Uri createQR(String text) throws WriterException, IOException {
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 480, 480);
		BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
		Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

		String filepath = context.getFilesDir().getAbsolutePath() + "/backup_qr.png";
		File file = new File(filepath);
		FileOutputStream stream = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
		stream.close();
		return Uri.fromFile(file);
	}

	public void decodeQR(){

	}
}
