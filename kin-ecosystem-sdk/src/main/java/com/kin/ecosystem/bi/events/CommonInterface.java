package com.kin.ecosystem.bi.events;

public interface CommonInterface extends CommonReadonly {
    void setEventId(String eventId);

    void setOs(String os);

    void setVersion(String version);

    void setLanguage(String language);

    void setCarrier(String carrier);

    void setDeviceId(String deviceId);

    void setEventType(Common.EventType eventType);

    void setUserId(String userId);

    void setTimestamp(Double timestamp);

    void setDeviceType(String deviceType);

    void setPlatform(Common.Platform platform);

    void setDeviceManufacturer(String deviceManufacturer);

    void setDeviceModel(String deviceModel);

}
