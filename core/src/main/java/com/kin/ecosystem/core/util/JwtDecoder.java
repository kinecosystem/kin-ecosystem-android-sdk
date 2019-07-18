package com.kin.ecosystem.core.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.kin.ecosystem.core.data.auth.JwtBody;
import com.kin.ecosystem.core.data.order.OfferJwtBody;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import org.json.JSONException;
import org.json.JSONObject;

public class JwtDecoder {

	private static final String ISS_KEY = "iss";
	private static final String USER_ID_KEY = "user_id";
	private static final String DEVICE_ID_KEY = "device_id";

	private static final String OFFER_OBJECT_KEY = "offer";
	private static final String SUB_KEY = "sub";
	private static final String ID_KEY = "id";



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
	public static OfferJwtBody getOfferJwtBody(@NonNull String jwt) throws JSONException, IllegalArgumentException {
		String body = decodeJwtBody(jwt);
		if (StringUtil.isEmpty(body)) {
			return null;
		}

		JSONObject object = new JSONObject(body);
		final OfferType type = OfferType.fromValue(object.getString(SUB_KEY));
		final String offerId = object.getJSONObject(OFFER_OBJECT_KEY).getString(ID_KEY);
		return new OfferJwtBody(offerId, type);
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
