package com.kin.ecosystem.data.user;

import android.support.annotation.NonNull;

public class UserInfoRepository implements UserInfoDataSource {

    private static UserInfoRepository instance = null;

    private final UserInfoDataSource localData;

    private UserInfoRepository(@NonNull UserInfoDataSource local) {
        this.localData = local;
    }

    public static void init(@NonNull UserInfoDataSource localData) {
        if (instance == null) {
            synchronized (UserInfoRepository.class) {
                instance = new UserInfoRepository(localData);
            }
        }
    }

    public static UserInfoRepository getInstance() {
        return instance;
    }

    @Override
    public boolean isConfirmedTOS() {
        return localData.isConfirmedTOS();
    }

    @Override
    public void setConfirmedTOS(boolean isConfirmedTOS) {

        localData.setConfirmedTOS(isConfirmedTOS);
    }
}
