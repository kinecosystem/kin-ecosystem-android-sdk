package com.kin.ecosystem.recovery.qr;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator.QRNotFoundInImageException;
import java.io.IOException;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

public class QRBarcodeGeneratorImplTest {

	private static final String TEST_DATA = "{\n"
		+ "  \"pkey\": \"GCJS54LFY5H5UXSAKLWP3GXCNKAZZLRAPO45B6PLAAINRVKJSWZGZAF4\",\n"
		+ "  \"seed\": \"cb60a6afa2427194f4fbdc19969dd2b34677e2cae5108d34f51970a43f47eacf36520ebe26c34064ab6d1cd29e9e8c362685651a81f0ce0525dd728028b7956e037545ec223b72d8\",\n"
		+ "  \"salt\": \"f16fa85a112efdd00eb0134239f53c37\"\n"
		+ "}";

	private static final String EXPECTED_TEXT_QR_IMAGE = "{\n"
		+ "  \"pkey\": \"GCJS54LFY5H5UXSAKLWP3GXCNKAZZLRAPO45B6PLAAINRVKJSWZGZAF4\",\n"
		+ "  \"seed\": \"c71d8965df716fb0a6edb53b5f4215f9f5b29552aef761e44b0d3fd9a26eb8fae3001e5be27e1d0df1f3baf72b2ddea38075cd0783d14e842c555d1b7264211546503fab7b647b09\",\n"
		+ "  \"salt\": \"ad1b920b16e4f7b519ac5117af77069d\"\n"
		+ "}";

	private static final String EXPECTED_TEXT_QR_IMAGE_TRY_HARDER = "{\n"
		+ "  \"pkey\": \"GCSFMCRAJ7UQU7MDA5TUBY3ROFYU6L6D2JPKXUM4YGSL2SLZOTKCS647\",\n"
		+ "  \"seed\": \"6f9e2fe953d37a0122347df582e57f9f7a2a7a91eccb3c5b13450c4ac7b94987ebd330e546b020e930062ed01c32d228f557e74979d3170d66644a6407ba56f70d9d508e2f4c5b7a\",\n"
		+ "  \"salt\": \"0586cdddfa57a2cfa8d02ca78103f3c7\"\n"
		+ "}";

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
			.generate(TEST_DATA);
		assertNotNull(uri);
		assertNotNull(fakeQRFileHandler.loadFile(uri));
	}

	@Test
	public void decodeQR_success() throws Exception {
		// test a QR that was being able to be decoded only with TRY_HARDER flag on in zxing library
		Bitmap bitmap = TestUtils.loadBitmapFromResource(this.getClass(), "backup_qr_try_harder.png");
		Uri uri = fakeQRFileHandler.saveFile(bitmap);
		String decodedQR = qrBarcodeGenerator.decodeQR(uri);
		assertThat(decodedQR, equalTo(EXPECTED_TEXT_QR_IMAGE_TRY_HARDER));

		Bitmap bitmap2 = TestUtils.loadBitmapFromResource(this.getClass(), "qr_test.png");
		Uri uri2 = fakeQRFileHandler.saveFile(bitmap2);
		String decodedQR2 = qrBarcodeGenerator.decodeQR(uri2);
		assertThat(decodedQR2, equalTo(EXPECTED_TEXT_QR_IMAGE));
	}

	@Test
	public void decodeQR_EmptyImage_NotFoundException() throws Exception {
		expectedEx.expect(QRNotFoundInImageException.class);
		Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
		Uri uri = fakeQRFileHandler.saveFile(bitmap);
		qrBarcodeGenerator.decodeQR(uri);
	}

	@Test
	public void generateAndDecode_success() throws Exception {
		Uri uri = qrBarcodeGenerator.generate(TEST_DATA);
		String decodedQR = qrBarcodeGenerator.decodeQR(uri);
		assertThat(decodedQR, equalTo(TEST_DATA));
	}

}