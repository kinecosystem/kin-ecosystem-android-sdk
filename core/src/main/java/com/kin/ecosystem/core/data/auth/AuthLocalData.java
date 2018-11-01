package com.kin.ecosystem.core.data.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.network.model.SignInData;
import com.kin.ecosystem.core.network.model.SignInData.SignInTypeEnum;


public class AuthLocalData implements AuthDataSource.Local {

	private static volatile AuthLocalData instance;

	private static final String SIGN_IN_PREF_NAME_FILE_KEY = "kinecosystem_sign_in_pref";

	private static final String JWT_KEY = "jwt";
	private static final String USER_ID_KEY = "user_id";
	private static final String ECOSYSTEM_USER_ID_KEY = "ecosystem_user_id";
	private static final String APP_ID_KEY = "app_id";
	private static final String DEVICE_ID_KEY = "device_id";
	private static final String PUBLIC_ADDRESS_KEY = "public_address";
	private static final String TYPE_KEY = "type";

	private static final String TOKEN_KEY = "token";
	private static final String TOKEN_EXPIRATION_DATE_KEY = "token_expiration_date";

	private final SharedPreferences signInSharedPreferences;

	private AuthLocalData(Context context) {
		this.signInSharedPreferences = context.getSharedPreferences(SIGN_IN_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE);
	}

	public static AuthLocalData getInstance(@NonNull Context context) {
		if (instance == null) {
			synchronized (AuthLocalData.class) {
				if (instance == null) {
					instance = new AuthLocalData(context);
				}
			}
		}

		return instance;
	}

	@Override
	public void setSignInData(@NonNull final SignInData signInData) {
		Editor editor = signInSharedPreferences.edit();
		editor.putString(DEVICE_ID_KEY, signInData.getDeviceId());
		editor.putString(PUBLIC_ADDRESS_KEY, signInData.getWalletAddress());
		editor.putString(TYPE_KEY, signInData.getSignInType().getValue());

		if (signInData.getSignInType() == SignInTypeEnum.JWT) {
			editor.putString(JWT_KEY, signInData.getJwt());
		} else {
			editor.putString(USER_ID_KEY, signInData.getUserId());
			editor.putString(APP_ID_KEY, signInData.getAppId());
		}
		editor.apply();
	}

	@Override
	public void setAuthToken(@NonNull final AuthToken authToken) {
		Editor editor = signInSharedPreferences.edit();
		editor.putString(TOKEN_KEY, authToken.getToken());
		editor.putString(APP_ID_KEY, authToken.getAppID());
		editor.putString(USER_ID_KEY, authToken.getUserID());
		editor.putString(ECOSYSTEM_USER_ID_KEY, authToken.getEcosystemUserID());
		editor.putString(TOKEN_EXPIRATION_DATE_KEY, authToken.getExpirationDate());
		editor.apply();
	}

	@Override
	public String getAppId() {
		return signInSharedPreferences.getString(APP_ID_KEY, null);
	}

	@Override
	public String getDeviceID() {
		return signInSharedPreferences.getString(DEVICE_ID_KEY, null);
	}

	@Override
	public String getUserID() {
		return signInSharedPreferences.getString(USER_ID_KEY, null);
	}

	@Override
	public String getEcosystemUserID() {
		return signInSharedPreferences.getString(ECOSYSTEM_USER_ID_KEY, null);
	}

	@Override
	public AuthToken getAuthTokenSync() {
		String token = signInSharedPreferences.getString(TOKEN_KEY, null);
		String appID = signInSharedPreferences.getString(APP_ID_KEY, null);
		String userID = getUserID();
		String ecosystemUserID = getEcosystemUserID();
		String expirationDate = signInSharedPreferences.getString(TOKEN_EXPIRATION_DATE_KEY, null);
		if (token != null && expirationDate != null) {
			return new AuthToken(token, expirationDate, appID, userID, ecosystemUserID);
		} else {
			return null;
		}
	}
}

