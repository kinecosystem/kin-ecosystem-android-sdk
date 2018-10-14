package com.kin.ecosystem.backup.qr;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import com.kin.ecosystem.backup.BuildConfig;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRFileUriHandlerImpl implements QRFileUriHandler {

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
		String filepath = context.getFilesDir().getAbsolutePath() + "qr_codes/backup_qr.png";
		File file = new File(filepath);
		FileOutputStream stream = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
		stream.close();
		return FileProvider.getUriForFile(
			context,
			BuildConfig.APPLICATION_ID,
			file);
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
