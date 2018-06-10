package com.kin.ecosystem.bi.events;

public interface CommonReadonly {
    String getEventId();

    String getOs();

    String getVersion();

    String getLanguage();

    String getCarrier();

    String getDeviceId();

    String getUserId();

    Double getTimestamp();

    Common.Platform getPlatform();

    String getDeviceManufacturer();

    String getDeviceModel();

}
