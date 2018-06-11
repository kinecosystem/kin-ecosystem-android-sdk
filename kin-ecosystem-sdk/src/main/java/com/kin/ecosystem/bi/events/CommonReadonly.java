package com.kin.ecosystem.bi.events;

import java.util.UUID;

public interface CommonReadonly {
    UUID getEventId();

    String getOs();

    String getVersion();

    String getLanguage();

    String getCarrier();

    String getDeviceId();

    String getUserId();

    Long getTimestamp();

    String getPlatform();

    String getDeviceManufacturer();

    String getDeviceModel();

}
