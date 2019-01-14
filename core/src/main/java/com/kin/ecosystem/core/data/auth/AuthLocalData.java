package com.kin.ecosystem.core.data.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.core.network.model.AccountInfo;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.JwtDecoder;
import org.json.JSONException;


public class AuthLocalData implements AuthDataSource.Local {

	private static volatile AuthLocalData instance;

	private static final String SIGN_IN_PREF_NAME_FILE_KEY = "kinecosystem_sign_in_pref";

	private static final String JWT_KEY = "jwt";
	private static final String USER_ID_KEY = "user_id";
	private static final String ECOSYSTEM_USER_ID_KEY = "ecosystem_user_id";
	private static final String APP_ID_KEY = "app_id";
	private static final String DEVICE_ID_KEY = "device_id";

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
	public void setJWT(@NonNull final JwtBody jwtBody) {
		Editor editor = signInSharedPreferences.edit();
		editor.putString(DEVICE_ID_KEY, jwtBody.getDeviceId());
		editor.putString(USER_ID_KEY, jwtBody.getUserId());
		editor.putString(APP_ID_KEY, jwtBody.getAppId());
		editor.apply();
	}

	@Override
	public String getJWT() {
		return signInSharedPreferences.getString(JWT_KEY, null);
	}

	@Override
	public void setAccountInfo(@NonNull AccountInfo accountInfo) {
		if (accountInfo.getAuthToken() != null) {
			setAuthToken(accountInfo.getAuthToken());
		}
	}

	private void setAuthToken(@NonNull final AuthToken authToken) {
		Editor editor = signInSharedPreferences.edit();
		editor.putString(TOKEN_KEY, authToken.getToken());
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

	@Override
	public void logout() {
		Editor editor = signInSharedPreferences.edit();
		editor.remove(JWT_KEY);
		editor.remove(USER_ID_KEY);
		editor.remove(ECOSYSTEM_USER_ID_KEY);
		editor.remove(DEVICE_ID_KEY);
		editor.remove(TOKEN_KEY);
		editor.remove(TOKEN_EXPIRATION_DATE_KEY);
		editor.apply();
	}
}

