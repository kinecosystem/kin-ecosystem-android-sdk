package com.kin.ecosystem.data.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class UserInfoLocalData implements UserInfoDataSource {

    private static volatile UserInfoLocalData instance;

    private static final String USER_INFO_PREF_NAME = "kinecosystem_user_info_pref";

    private static final String IS_CONFIRMED_TOS_KEY = "is_confirmed_tos";

    private final SharedPreferences userInfoSharedPreferences;

    private UserInfoLocalData(Context context) {
        this.userInfoSharedPreferences = context.getSharedPreferences(USER_INFO_PREF_NAME, Context.MODE_PRIVATE);

    }

    public static UserInfoLocalData getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (UserInfoLocalData.class) {
                instance = new UserInfoLocalData(context);
            }
        }

        return instance;
    }

    @Override
    public boolean isConfirmedTOS() {
        return userInfoSharedPreferences.getBoolean(IS_CONFIRMED_TOS_KEY, false);
    }

    @Override
    public void setConfirmedTOS(boolean isConfirmedTOS) {
        userInfoSharedPreferences.edit().putBoolean(IS_CONFIRMED_TOS_KEY, isConfirmedTOS).apply();
    }
}
