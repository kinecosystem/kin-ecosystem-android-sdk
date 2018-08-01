package com.kin.ecosystem.core.bi.events;

import java.util.UUID;
import com.kin.ecosystem.core.bi.EventsStore.DynamicValue;

public class CommonProxy implements CommonInterface {
    public Common snapshot() {
        return new Common(
            this.getEventId(),
            this.getVersion(),
            this.getUserId(),
            this.getTimestamp());
    }

    private UUID eventId;
    private DynamicValue<UUID> dynamicEventId;
    public UUID getEventId() {
        return this.eventId != null ? this.eventId : this.dynamicEventId.get();
    }
    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
    public void setEventId(DynamicValue<UUID> eventId) {
        this.dynamicEventId = eventId;
    }

    private String version;
    private DynamicValue<String> dynamicVersion;
    public String getVersion() {
        return this.version != null ? this.version : this.dynamicVersion.get();
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public void setVersion(DynamicValue<String> version) {
        this.dynamicVersion = version;
    }

    private String userId;
    private DynamicValue<String> dynamicUserId;
    public String getUserId() {
        return this.userId != null ? this.userId : this.dynamicUserId.get();
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserId(DynamicValue<String> userId) {
        this.dynamicUserId = userId;
    }

    private Long timestamp;
    private DynamicValue<Long> dynamicTimestamp;
    public Long getTimestamp() {
        return this.timestamp != null ? this.timestamp : this.dynamicTimestamp.get();
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public void setTimestamp(DynamicValue<Long> timestamp) {
        this.dynamicTimestamp = timestamp;
    }

    private String platform;
    private DynamicValue<String> dynamicPlatform;
    public String getPlatform() {
        return this.platform != null ? this.platform : this.dynamicPlatform.get();
    }
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public void setPlatform(DynamicValue<String> platform) {
        this.dynamicPlatform = platform;
    }

}
