package com.kin.ecosystem.recovery.qr;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;

public class QRFileUriHandlerImplTest {

	private Context context = InstrumentationRegistry.getContext();
	private QRFileUriHandlerImpl fileUriHandler;

	@Before
	public void setup() {
		fileUriHandler = new QRFileUriHandlerImpl(context);
	}

	@Test
	public void saveAndLoad() throws Exception {
		Bitmap bitmap = TestUtils.loadBitmapFromResource(this.getClass(), "qr_test.png");
		Uri uri = fileUriHandler.saveFile(bitmap);
		Bitmap loadedBitmap = fileUriHandler.loadFile(uri);
		assertThat(bitmap.sameAs(loadedBitmap), equalTo(true));
	}

	@Test
	public void saveFile_Success() throws Exception {
		String appPackageName = context.getPackageName();
		Bitmap bitmap = TestUtils.loadBitmapFromResource(this.getClass(), "qr_test.png");
		Uri uri = fileUriHandler.saveFile(bitmap);
		assertThat(uri, notNullValue());
		assertThat(uri.toString(), equalTo("content://" + appPackageName + ".KinRecoveryFileProvider/qr_codes/backup_qr.png"));
	}

}