
package com.kin.ecosystem.core.bi.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * common properties for all events
 * 
 */
public class Common implements CommonInterface {
    public static final String PLATFORM = "Android";
    public static final String SCHEMA_VERSION = "59ba36a8e21c628c3a0a637784021db43fe5b008";

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("schema_version")
    @Expose
    private String schemaVersion = SCHEMA_VERSION;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_id")
    @Expose
    private UUID eventId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("version")
    @Expose
    private String version;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("user_id")
    @Expose
    private String userId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("platform")
    @Expose
    private String platform = PLATFORM;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Common() {
    }

    /**
     * 
     * @param eventId
     * @param schemaVersion
     * @param version
     * @param deviceId
     * @param userId
     * @param timestamp
     */
    public Common(String schemaVersion, UUID eventId, String version, String deviceId, String userId, Long timestamp) {
        super();
        this.schemaVersion = schemaVersion;
        this.eventId = eventId;
        this.version = version;
        this.deviceId = deviceId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public UUID getEventId() {
        return eventId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getVersion() {
        return version;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

}
