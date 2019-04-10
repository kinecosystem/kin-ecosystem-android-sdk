package com.kin.ecosystem.core.bi.events;

import java.util.UUID;

public interface CommonReadonly {
    String getSchemaVersion();

    UUID getEventId();

    String getVersion();

    String getDeviceId();

    String getUserId();

    Long getTimestamp();

    String getPlatform();

}
