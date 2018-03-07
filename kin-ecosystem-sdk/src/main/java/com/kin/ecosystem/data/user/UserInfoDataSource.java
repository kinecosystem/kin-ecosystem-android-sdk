package com.kin.ecosystem.data.user;

public interface UserInfoDataSource {

    boolean isConfirmedTOS();

    void setConfirmedTOS(boolean isConfirmedTOS);
}
