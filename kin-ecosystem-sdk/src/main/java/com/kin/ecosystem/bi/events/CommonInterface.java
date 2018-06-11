package com.kin.ecosystem.bi.events;

import java.util.UUID;

public interface CommonInterface extends CommonReadonly {
    void setEventId(UUID eventId);

    void setOs(String os);

    void setVersion(String version);

    void setLanguage(String language);

    void setCarrier(String carrier);

    void setDeviceId(String deviceId);

    void setUserId(String userId);

    void setTimestamp(Long timestamp);

    void setPlatform(String platform);

    void setDeviceManufacturer(String deviceManufacturer);

    void setDeviceModel(String deviceModel);

}
