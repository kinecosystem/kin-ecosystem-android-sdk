package com.kin.ecosystem.core.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.kin.ecosystem.core.data.auth.JwtBody;
import org.json.JSONException;
import org.json.JSONObject;

public class JwtDecoder {

	private static final String ISS_KEY = "iss";
	private static final String USER_ID_KEY = "user_id";
	private static final String DEVICE_ID_KEY = "device_id";
	private static final int JWT_SPLIT_PARTS_SIZE = 3;

	@Nullable
	public static JwtBody getJwtBody(@NonNull String jwt) throws JSONException, IllegalArgumentException {
		String body = decodeJwtBody(jwt);
		if (StringUtil.isEmpty(body)) {
			return null;
		}

		JSONObject object = new JSONObject(body);
		return new JwtBody(object.getString(ISS_KEY),
			object.getString(USER_ID_KEY),
			object.getString(DEVICE_ID_KEY));
	}

	@Nullable
	private static String decodeJwtBody(@NonNull String jwt) {
		String[] splitJWT = jwt.split("\\.");
		if (splitJWT.length != JWT_SPLIT_PARTS_SIZE) {
			return null;
		}
		String base64EncodedHeader = splitJWT[1];
		return new String(Base64.decode(base64EncodedHeader, Base64.DEFAULT));

	}
}
