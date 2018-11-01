package com.kin.ecosystem.recovery.qr;


import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.io.IOException;

interface QRFileUriHandler {

	@NonNull
	Bitmap loadFile(@NonNull Uri uri) throws IOException;

	@NonNull
	Uri saveFile(@NonNull Bitmap image) throws IOException;
}
