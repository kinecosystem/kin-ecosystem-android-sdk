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

public class AuthLocalData implements AuthDataSource {

    private static volatile AuthLocalData instance;

    private static final String SIGN_IN_PREF_NAME = "kinecosystem_sign_in_pref";

    private static final String JWT_KEY = "jwt";
    private static final String USER_ID_KEY = "user_id";
    private static final String APP_ID_KEY = "app_id";
    private static final String DEVICE_ID_KEY = "device_id";
    private static final String PUBLIC_ADDRESS_KEY = "public_address";
    private static final String TYPE_KEY = "type";

    private static final String TOKEN_KEY = "token";
    private static final String TOKEN_EXPIRATION_DATE_KEY = "token_expiration_date";

    private final SharedPreferences signInSharedPreferences;
    private final ExecutorsUtil executorsUtil;

    private AuthLocalData(Context context, @NonNull ExecutorsUtil executorsUtil) {
        this.signInSharedPreferences = context.getSharedPreferences(SIGN_IN_PREF_NAME, Context.MODE_PRIVATE);
        this.executorsUtil = executorsUtil;
    }

    public static AuthLocalData getInstance(@NonNull Context context, @NonNull ExecutorsUtil executorsUtil) {
        if (instance == null) {
            synchronized (AuthLocalData.class) {
                instance = new AuthLocalData(context, executorsUtil);
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
                editor.putString(APP_ID_KEY, signInData.getUserId());
                editor.putString(DEVICE_ID_KEY, signInData.getUserId());
                editor.putString(PUBLIC_ADDRESS_KEY, signInData.getUserId());
                if (signInData.getSignInType() == SignInTypeEnum.JWT) {
                    editor.putString(TYPE_KEY, SignInTypeEnum.JWT.getValue());
                }
                editor.commit();
            }
        };

        executorsUtil.diskIO().execute(command);
    }

    @Override
    public void getSignInData(@NonNull final Callback<SignInData> callback) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                SignInTypeEnum signInType;
                if ((signInType = getType()) != null) {

                    final SignInData signInData = new SignInData();
                    signInData.setUserId(signInSharedPreferences.getString(USER_ID_KEY, null));
                    signInData.appId(signInSharedPreferences.getString(APP_ID_KEY, null));
                    signInData.deviceId(signInSharedPreferences.getString(DEVICE_ID_KEY, null));
                    signInData.publicAddress(signInSharedPreferences.getString(PUBLIC_ADDRESS_KEY, null));
                    signInData.setSignInType(signInType);

                    if (signInType == SignInTypeEnum.JWT) {
                        signInData.setJwt(signInSharedPreferences.getString(JWT_KEY, null));
                    }
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(signInData);
                        }
                    });

                } else {
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(new DataNotAvailableException());
                        }
                    });
                }
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
                editor.putString(TOKEN_EXPIRATION_DATE_KEY, authToken.getExpirationDate());
                editor.commit();
            }
        };
        executorsUtil.diskIO().execute(command);
    }

    @Override
    public void getAuthToken(@NonNull final Callback<AuthToken> callback) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                final String token = signInSharedPreferences.getString(TOKEN_KEY, null);
                final String expirationDate = signInSharedPreferences.getString(TOKEN_EXPIRATION_DATE_KEY, null);
                executorsUtil.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (token != null && expirationDate != null) {
                            callback.onResponse(new AuthToken().token(token).expirationDate(expirationDate));
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
        String expirationDate = signInSharedPreferences.getString(TOKEN_EXPIRATION_DATE_KEY, null);
        if (token != null && expirationDate != null) {
            return new AuthToken().token(token).expirationDate(expirationDate);
        } else {
            return null;
        }
    }

    private SignInTypeEnum getType() {
        String signInType = signInSharedPreferences.getString(TYPE_KEY, null);

        return SignInTypeEnum.fromValue(signInType);
    }

}
