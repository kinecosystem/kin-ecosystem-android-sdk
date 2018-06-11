package com.kin.ecosystem.bi.events;

import java.util.UUID;
import com.kin.ecosystem.bi.EventsStore;

public class CommonProxy implements CommonInterface {
    public Common snapshot() {
        return new Common(
            this.getEventId(),
            this.getVersion(),
            this.getUserId(),
            this.getTimestamp());
    }

    private UUID eventId;
    private EventsStore.DynamicValue<UUID> dynamicEventId;
    public UUID getEventId() {
        return this.eventId != null ? this.eventId : this.dynamicEventId.get();
    }
    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
    public void setEventId(EventsStore.DynamicValue<UUID> eventId) {
        this.dynamicEventId = eventId;
    }

    private String version;
    private EventsStore.DynamicValue<String> dynamicVersion;
    public String getVersion() {
        return this.version != null ? this.version : this.dynamicVersion.get();
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public void setVersion(EventsStore.DynamicValue<String> version) {
        this.dynamicVersion = version;
    }

    private String userId;
    private EventsStore.DynamicValue<String> dynamicUserId;
    public String getUserId() {
        return this.userId != null ? this.userId : this.dynamicUserId.get();
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserId(EventsStore.DynamicValue<String> userId) {
        this.dynamicUserId = userId;
    }

    private Long timestamp;
    private EventsStore.DynamicValue<Long> dynamicTimestamp;
    public Long getTimestamp() {
        return this.timestamp != null ? this.timestamp : this.dynamicTimestamp.get();
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public void setTimestamp(EventsStore.DynamicValue<Long> timestamp) {
        this.dynamicTimestamp = timestamp;
    }

    private String platform;
    private EventsStore.DynamicValue<String> dynamicPlatform;
    public String getPlatform() {
        return this.platform != null ? this.platform : this.dynamicPlatform.get();
    }
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public void setPlatform(EventsStore.DynamicValue<String> platform) {
        this.dynamicPlatform = platform;
    }

}
