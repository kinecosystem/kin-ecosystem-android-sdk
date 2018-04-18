package com.ecosystem.kin.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import com.ecosystem.kin.app.BuildConfig;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.network.model.SignInData.SignInTypeEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.UUID;

public class SignInRepo {

    private final static String USER_PREFERENCE_FILE_KEY = "USER_PREFERENCE_FILE_KEY";
    private final static String USER_UUID_KEY = "USER_UUID_KEY";
    private static final String DEVICE_UUID_KEY = "DEVICE_UUID_KEY";
    private static final long DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;
    public static final String JWT_SUBJECT_REGISTER = "register";
    public static final String JWT_KEY_USER_ID = "user_id";
    public static final String JWT_HEADER_KEYID = "keyid";
    public static final String ALGORITHM_RSA = "RSA";
    public static final String SECURITY_PROVIDER_BC = "BC";


    public static SignInData getWhitelistSignInData(Context context, @NonNull String appId, @NonNull String apiKey) {
        SignInData signInData = createSignInDataWithDeviceID(context);
        signInData.signInType(SignInTypeEnum.WHITELIST)
            .appId(appId)
            .userId(getUserId(context))
            .apiKey(apiKey);
        return signInData;
    }

    public static SignInData getJWTSignInData(Context context, @Nullable String jwt) {
        SignInData signInData = createSignInDataWithDeviceID(context);
        signInData.signInType(SignInTypeEnum.JWT);

        if (TextUtils.isEmpty(jwt)) {
            signInData.jwt(generateExampleJWT(context));
        } else {
            signInData.jwt(jwt);
        }
        return signInData;
    }

    private static SignInData createSignInDataWithDeviceID(Context context) {
        SignInData signInData = new SignInData();

        SharedPreferences sharedPreferences = getSharedPreferences(context);

        String deviceUUID = sharedPreferences.getString(DEVICE_UUID_KEY, null);
        if (deviceUUID == null) {
            deviceUUID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(DEVICE_UUID_KEY, deviceUUID).apply();
        }
        signInData.deviceId(deviceUUID);
        return signInData;
    }

    @NonNull
    private static String getAppId() {
        return BuildConfig.SAMPLE_APP_ID;
    }

    @NonNull
    private static String getPrivateKeyForJWT() {
        return BuildConfig.RS512_PRIVATE_KEY;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context
            .getSharedPreferences(USER_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    private static String getUserId(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String userID = sharedPreferences.getString(USER_UUID_KEY, null);
        if (userID == null) {
            userID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(USER_UUID_KEY, userID).apply();
        }
        return userID;
    }

    private static String generateExampleJWT(Context context) {
        String jwt = Jwts.builder()
            .setHeaderParam(JWT_HEADER_KEYID, "1")
            .setIssuer(getAppId())
            .setSubject(JWT_SUBJECT_REGISTER)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + DAY_IN_MILLISECONDS))
            .claim(JWT_KEY_USER_ID, getUserId(context))
            .signWith(SignatureAlgorithm.RS512, getRS512PrivateKey()).compact();
        return jwt;
    }

    @Nullable
    private static PrivateKey getRS512PrivateKey() {
        PrivateKey privateKey = null;
        KeyFactory keyFactory;

        byte[] bytes = Base64.decode(getPrivateKeyForJWT(), Base64.NO_WRAP);
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM_RSA, SECURITY_PROVIDER_BC);
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
}
