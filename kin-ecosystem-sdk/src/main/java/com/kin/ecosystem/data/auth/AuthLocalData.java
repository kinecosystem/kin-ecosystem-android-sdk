package com.kin.ecosystem.data.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.exception.DataNotAvailableException;
import com.kin.ecosystem.network.model.AuthToken;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.network.model.SignInData.SignInTypeEnum;
import com.kin.ecosystem.util.ExecutorsUtil;

public class AuthLocalData implements AuthDataSource.Local {

    private static volatile AuthLocalData instance;

    private static final String SIGN_IN_PREF_NAME_FILE_KEY = "kinecosystem_sign_in_pref";

    private static final String JWT_KEY = "jwt";
    private static final String USER_ID_KEY = "user_id";
    private static final String APP_ID_KEY = "app_id";
    private static final String DEVICE_ID_KEY = "device_id";
    private static final String PUBLIC_ADDRESS_KEY = "public_address";
    private static final String TYPE_KEY = "type";

    private static final String TOKEN_KEY = "token";
    private static final String TOKEN_EXPIRATION_DATE_KEY = "token_expiration_date";

    private static final String IS_ACTIVATED_KEY = "is_activated";

    private final SharedPreferences signInSharedPreferences;
    private final ExecutorsUtil executorsUtil;

    private AuthLocalData(Context context, @NonNull ExecutorsUtil executorsUtil) {
        this.signInSharedPreferences = context.getSharedPreferences(SIGN_IN_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE);
        this.executorsUtil = executorsUtil;
    }

    public static AuthLocalData getInstance(@NonNull Context context, @NonNull ExecutorsUtil executorsUtil) {
        if (instance == null) {
            synchronized (AuthLocalData.class) {
                if (instance == null) {
                    instance = new AuthLocalData(context, executorsUtil);
                }
            }
        }

        return instance;
    }

    @Override
    public void setSignInData(@NonNull final SignInData signInData) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                Editor editor = signInSharedPreferences.edit();
                editor.putString(USER_ID_KEY, signInData.getUserId());
                editor.putString(DEVICE_ID_KEY, signInData.getDeviceId());
                editor.putString(PUBLIC_ADDRESS_KEY, signInData.getWalletAddress());
                editor.putString(TYPE_KEY, signInData.getSignInType().getValue());

                if (signInData.getSignInType() == SignInTypeEnum.JWT) {
                    editor.putString(JWT_KEY, signInData.getJwt());
                }
                else {
                    editor.putString(APP_ID_KEY, signInData.getAppId());
                }
                editor.commit();
            }
        };

        executorsUtil.diskIO().execute(command);
    }

    @Override
    public void setAuthToken(@NonNull final AuthToken authToken) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                Editor editor = signInSharedPreferences.edit();
                editor.putString(TOKEN_KEY, authToken.getToken());
                editor.putString(APP_ID_KEY, authToken.getAppID());
                editor.putBoolean(IS_ACTIVATED_KEY, authToken.isActivated());
                editor.putString(TOKEN_EXPIRATION_DATE_KEY, authToken.getExpirationDate());
                editor.commit();
            }
        };
        executorsUtil.diskIO().execute(command);
    }

    @Override
    public void getAppId(@NonNull final Callback<String> callback) {
        final Runnable command = new Runnable() {
            @Override
            public void run() {
                final String appID = signInSharedPreferences.getString(APP_ID_KEY, null);
                executorsUtil.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (appID != null) {
                            callback.onResponse(appID);
                        } else {
                            callback.onFailure(new DataNotAvailableException());
                        }
                    }
                });
            }
        };
        executorsUtil.diskIO().execute(command);
    }

    @Override
    public AuthToken getAuthTokenSync() {
        String token = signInSharedPreferences.getString(TOKEN_KEY, null);
        String appID = signInSharedPreferences.getString(APP_ID_KEY, null);
        boolean isActivated = signInSharedPreferences.getBoolean(IS_ACTIVATED_KEY, false);
        String expirationDate = signInSharedPreferences.getString(TOKEN_EXPIRATION_DATE_KEY, null);
        if (token != null && expirationDate != null) {
            return new AuthToken(token, isActivated, expirationDate, appID);
        } else {
            return null;
        }
    }

    @Override
    public boolean isActivated() {
        return signInSharedPreferences.getBoolean(IS_ACTIVATED_KEY, false);
    }

    @Override
    public void activateAccount() {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                Editor editor = signInSharedPreferences.edit();
                editor.putBoolean(IS_ACTIVATED_KEY, true).commit();
            }
        };
        executorsUtil.diskIO().execute(command);
    }
}

