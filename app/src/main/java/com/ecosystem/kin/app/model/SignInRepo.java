package com.ecosystem.kin.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.ecosystem.kin.app.BuildConfig;
import com.ecosystem.kin.app.JwtUtil;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.network.model.SignInData.SignInTypeEnum;
import java.util.UUID;

public class SignInRepo {

    private final static String USER_PREFERENCE_FILE_KEY = "USER_PREFERENCE_FILE_KEY";
    private final static String USER_UUID_KEY = "USER_UUID_KEY";
    private static final String DEVICE_UUID_KEY = "DEVICE_UUID_KEY";

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
            signInData.jwt(JwtUtil.generateSignInExampleJWT(getAppId(), getUserId(context)));
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

    private static SharedPreferences getSharedPreferences(Context context) {
        return context
            .getSharedPreferences(USER_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String userID = sharedPreferences.getString(USER_UUID_KEY, null);
        if (userID == null) {
            userID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(USER_UUID_KEY, userID).apply();
        }
        return userID;
    }

}
