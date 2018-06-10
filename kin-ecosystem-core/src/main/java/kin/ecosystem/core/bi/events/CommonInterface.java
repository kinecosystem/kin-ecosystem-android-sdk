package kin.ecosystem.core.bi.events;

public interface CommonInterface extends CommonReadonly {
    void setEventId(String eventId);

    void setOs(String os);

    void setVersion(String version);

    void setLanguage(String language);

    void setCarrier(String carrier);

    void setDeviceId(String deviceId);

    void setEventType(Common.EventType eventType);

    void setLatitude(String latitude);

    void setUserId(String userId);

    void setTimestamp(Double timestamp);

    void setCity(String city);

    void setDeviceType(String deviceType);

    void setLongitude(String longitude);

    void setCountry(String country);

    void setIpAddress(String ipAddress);

    void setPlatform(Common.Platform platform);

    void setRegion(String region);

    void setDeviceManufacturer(String deviceManufacturer);

    void setDeviceModel(String deviceModel);

    void setIngestTimestamp(Double ingestTimestamp);

}
