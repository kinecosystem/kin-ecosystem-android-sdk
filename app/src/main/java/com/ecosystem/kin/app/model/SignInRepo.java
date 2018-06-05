package com.ecosystem.kin.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.ecosystem.kin.app.BuildConfig;
import com.ecosystem.kin.app.JwtUtil;
import com.kin.ecosystem.data.model.WhitelistData;
import java.util.UUID;

public class SignInRepo {

    private final static String USER_PREFERENCE_FILE_KEY = "USER_PREFERENCE_FILE_KEY";
    private final static String USER_UUID_KEY = "USER_UUID_KEY";

    public static WhitelistData getWhitelistSignInData(Context context, @NonNull String appId, @NonNull String apiKey) {
        return new WhitelistData(getUserId(context), appId, apiKey);
    }

    public static String getJWT(Context context) {
        return JwtUtil.generateSignInExampleJWT(getAppId(), getUserId(context));
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
