package com.kin.ecosystem.recovery.qr;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;

public class QRFileUriHandlerImplTest {

	private QRFileUriHandlerImpl fileUriHandler;

	@Before
	public void setup() {
		fileUriHandler = new QRFileUriHandlerImpl(InstrumentationRegistry.getContext());
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
		Bitmap bitmap = TestUtils.loadBitmapFromResource(this.getClass(), "qr_test.png");
		Uri uri = fileUriHandler.saveFile(bitmap);
		assertThat(uri, notNullValue());
		assertThat(uri.toString(), equalTo("content://com.kin.ecosystem.backup/qr_codes/backup_qr.png"));
	}

}