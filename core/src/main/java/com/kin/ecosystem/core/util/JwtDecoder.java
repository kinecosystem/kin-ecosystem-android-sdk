package com.kin.ecosystem.core.util;

import android.util.Base64;
import com.kin.ecosystem.core.data.auth.JwtBody;
import org.json.JSONException;
import org.json.JSONObject;

public class JwtDecoder {

	private static final String ISS_KEY = "iss";
	private static final String USER_ID_KEY = "user_id";
	private static final String DEVICE_ID_KEY = "device_id";

	public static JwtBody getJwtBody(String jwt) throws JSONException {
		String header = decodeJwtBody(jwt);
		JSONObject object = new JSONObject(header);
		return new JwtBody(object.getString(ISS_KEY),
			object.getString(USER_ID_KEY),
			object.getString(DEVICE_ID_KEY));
	}

	private static String decodeJwtBody(String jwt) {
		String[] splitJWT = jwt.split("\\.");
		String base64EncodedHeader = splitJWT[1];
		return new String(Base64.decode(base64EncodedHeader, Base64.DEFAULT));
	}
}
