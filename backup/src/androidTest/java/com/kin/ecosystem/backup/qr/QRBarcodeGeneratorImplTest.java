package com.kin.ecosystem.backup.qr;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.kin.ecosystem.backup.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;
import java.io.IOException;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

public class QRBarcodeGeneratorImplTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	private QRBarcodeGeneratorImpl qrBarcodeGenerator;

	private FakeQRFileUriHandler fakeQRFileHandler;

	class FakeQRFileUriHandler implements QRFileUriHandler {

		private int counter = 0;
		private HashMap<Uri, Bitmap> map = new HashMap<>();

		@NonNull
		@Override
		public Bitmap loadFile(@NonNull Uri uri) throws IOException {
			return map.get(uri);
		}

		@NonNull
		@Override
		public Uri saveFile(@NonNull Bitmap image) throws IOException {
			Uri fakeUri = Uri.parse("file://test/" + counter + ".png");
			map.put(fakeUri, image);
			return fakeUri;
		}
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		fakeQRFileHandler = new FakeQRFileUriHandler();
		qrBarcodeGenerator = new QRBarcodeGeneratorImpl(fakeQRFileHandler);
	}

	@Test
	public void generate_success() throws Exception {
		Uri uri = qrBarcodeGenerator
			.generate("SC76EXP6QIVRASGA4EUCXQAOGJTQUATEI64AA5ZUULUKBOLIKJF46TL6");
		assertNotNull(uri);
		assertNotNull(fakeQRFileHandler.loadFile(uri));
	}

	@Test
	public void decodeQR_success() throws Exception {
		Bitmap bitmap = TestUtils.loadBitmapFromResource(this.getClass(), "test_qr.png");
		String decodedQR = qrBarcodeGenerator.decodeQR(bitmap);
		assertThat(decodedQR, equalTo("SDUMNNJHYTSGBENQLL6LQF6CZUU64V7RALXFQENR22NK7M3GPWSDCZU6"));
	}

	@Test
	public void decodeQR_error() throws Exception {
		expectedEx.expect(QRBarcodeGeneratorException.class);
		expectedEx.expectCause(isA(IllegalStateException.class));
		//empty bitmap
		Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
		String decodedQR = qrBarcodeGenerator.decodeQR(bitmap);
	}

	@Test
	public void generateAndDecode_success() throws Exception {
		String data = "SDFMH6MW6JTIUHIVU3UABZF7I7GFWMYXWMQEJQNFIBF4ZVWBSCKMDYQL";
		Uri uri = qrBarcodeGenerator.generate(data);
		Bitmap bitmap = fakeQRFileHandler.loadFile(uri);
		String decodedQR = qrBarcodeGenerator.decodeQR(bitmap);
		assertThat(decodedQR, equalTo(data));
	}

}