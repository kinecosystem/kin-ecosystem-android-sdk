
package com.kin.ecosystem.bi.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.UUID;


/**
 * common properties for all events
 * 
 */
public class Common implements CommonInterface {
    public static final String PLATFORM = "Android";

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
     * @param version
     * @param userId
     * @param timestamp
     */
    public Common(UUID eventId, String version, String userId, Long timestamp) {
        super();
        this.eventId = eventId;
        this.version = version;
        this.userId = userId;
        this.timestamp = timestamp;
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
