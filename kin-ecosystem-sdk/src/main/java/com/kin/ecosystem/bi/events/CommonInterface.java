package com.kin.ecosystem.bi.events;

import java.util.UUID;

public interface CommonInterface extends CommonReadonly {
    void setEventId(UUID eventId);

    void setVersion(String version);

    void setUserId(String userId);

    void setTimestamp(Long timestamp);

    void setPlatform(String platform);

}
