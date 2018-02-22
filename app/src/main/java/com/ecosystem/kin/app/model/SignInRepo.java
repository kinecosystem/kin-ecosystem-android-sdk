package com.ecosystem.kin.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.network.model.SignInData.SignInTypeEnum;
import java.util.UUID;

public class SignInRepo {

    private final static String USER_PREFERENCE_FILE_KEY = "USER_PREFERENCE_FILE_KEY";
    private final static String USER_UUID_KEY = "USER_UUID_KEY";
    private static final String DEVICE_UUID = "DEVICE_UUID_KEY";


    public static SignInData getSignInData(Context context) {
        SignInData signInData;
        SharedPreferences sharedPreferences = context
            .getSharedPreferences(USER_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString(USER_UUID_KEY, null);
        String deviceUUID = sharedPreferences.getString(DEVICE_UUID, null);
        if (deviceUUID == null) {
            deviceUUID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(DEVICE_UUID, deviceUUID).apply();
        }
        if (userID == null) {
            userID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(USER_UUID_KEY, userID).apply();
        }

        signInData = new SignInData()
            .signInType(SignInTypeEnum.WHITELIST)
            .appId("SampleApp")
            .deviceId(deviceUUID)
            .userId(userID);

        return signInData;
    }


}
