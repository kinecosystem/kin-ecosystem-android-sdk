package com.kin.ecosystem.backup.qr;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRFileUriHandlerImpl implements QRFileUriHandler {

	private static final String RELATIVE_PATH_FILENAME_QR_IMAGE = "/qr_codes/backup_qr.png";
	private final Context context;

	public QRFileUriHandlerImpl(@NonNull Context context) {
		this.context = context;
	}

	@NonNull
	@Override
	public Bitmap loadFile(@NonNull Uri uri) throws IOException {
		ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
		if (pfd != null) {
			FileDescriptor fd = pfd.getFileDescriptor();
			Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
			if (bitmap != null) {
				return bitmap;
			}
		}
		throw new IOException("decoding file as bitmap failed.");
	}

	@NonNull
	@Override
	public Uri saveFile(@NonNull Bitmap bitmap) throws IOException {
		File file = getOrCreateSaveFile();
		FileOutputStream stream = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
		stream.close();
		return FileProvider.getUriForFile(
			context,
			"com.kin.ecosystem.backup",
			file);
	}

	@NonNull
	private File getOrCreateSaveFile() throws IOException {
		String filepath = context.getFilesDir().getAbsolutePath() + RELATIVE_PATH_FILENAME_QR_IMAGE;
		File file = new File(filepath);
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				boolean dirCreated = file.getParentFile().mkdir();
				if (!dirCreated) {
					throw new IOException("Cannot create folder at target location.");
				}
			}
			boolean fileCreated = file.createNewFile();
			if (!fileCreated) {
				throw new IOException("Cannot create file at target location.");
			}
		}
		return file;
	}

	@NonNull
	public Intent getShareableIntent(@NonNull Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setType("image/*");
		return intent;
	}
}
