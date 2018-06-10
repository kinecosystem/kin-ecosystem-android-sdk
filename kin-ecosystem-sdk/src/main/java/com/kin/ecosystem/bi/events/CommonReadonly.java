package com.kin.ecosystem.bi.events;

public interface CommonReadonly {
    String getEventId();

    String getOs();

    String getVersion();

    String getLanguage();

    String getCarrier();

    String getDeviceId();

    Common.EventType getEventType();

    String getLatitude();

    String getUserId();

    Double getTimestamp();

    String getCity();

    String getDeviceType();

    String getLongitude();

    String getCountry();

    String getIpAddress();

    Common.Platform getPlatform();

    String getRegion();

    String getDeviceManufacturer();

    String getDeviceModel();

    Double getIngestTimestamp();

}
