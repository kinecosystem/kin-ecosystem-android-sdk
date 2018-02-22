package com.ecosystem.kin.app;

import android.app.Application;

import android.content.SharedPreferences;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.InitializeException;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.network.model.SignInData.SignInTypeEnum;
import java.util.UUID;


public class App extends Application {

    private static final String SAMPLE_APP_SHARE_PREF = "sample_app_share_pref";
    private static final String DEVICE_UUID = "device_uuid";

    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(SAMPLE_APP_SHARE_PREF, MODE_PRIVATE);
        String deviceUUID = getDeviceID();
        String userID = "123";
        try {
            SignInData signInData = new SignInData()
                .signInType(SignInTypeEnum.WHITELIST)
                .appId("SampleApp")
                .deviceId(deviceUUID)
                .userId(userID);

            Kin.start(getApplicationContext(), signInData);
        } catch (InitializeException e) {
            e.printStackTrace();
        }

    }

    private String getDeviceID() {
        String deviceUUID = sharedPreferences.getString(DEVICE_UUID, null);
        if(deviceUUID == null) {
            deviceUUID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(DEVICE_UUID, deviceUUID).apply();
        }
        return deviceUUID;
    }
}
