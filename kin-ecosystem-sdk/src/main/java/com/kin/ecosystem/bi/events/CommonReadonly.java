package com.kin.ecosystem.bi.events;

public interface CommonReadonly {
    String getEventId();

    String getOs();

    String getVersion();

    String getLanguage();

    String getCarrier();

    String getDeviceId();

    Common.EventType getEventType();

    String getUserId();

    Double getTimestamp();

    String getDeviceType();

    Common.Platform getPlatform();

    String getDeviceManufacturer();

    String getDeviceModel();

}
