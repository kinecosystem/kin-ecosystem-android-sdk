package com.kin.ecosystem.backup.qr;


import android.graphics.Bitmap;
import android.net.Uri;

public interface FileUriHandler {

	Bitmap loadFile(Uri uri);

	Uri saveFile(Bitmap image);
}
