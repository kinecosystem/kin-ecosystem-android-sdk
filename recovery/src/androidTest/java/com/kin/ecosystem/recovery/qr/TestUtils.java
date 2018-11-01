package com.kin.ecosystem.recovery.qr;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;

public class TestUtils {

	static Bitmap loadBitmapFromResource(Class clazz, String res) {
		InputStream is = clazz.getClassLoader()
			.getResourceAsStream(res);
		return BitmapFactory.decodeStream(is);
	}
}
