
package com.kin.ecosystem.bi.events;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * common properties for all events
 * 
 */
public class Common implements CommonInterface {

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_id")
    @Expose
    private String eventId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("os")
    @Expose
    private String os;
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
    @SerializedName("language")
    @Expose
    private String language;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("carrier")
    @Expose
    private String carrier;
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
    @SerializedName("event_type")
    @Expose
    private Common.EventType eventType;
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
    private Double timestamp;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("platform")
    @Expose
    private Common.Platform platform;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("device_manufacturer")
    @Expose
    private String deviceManufacturer;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("device_model")
    @Expose
    private String deviceModel;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Common() {
    }

    /**
     * 
     * @param deviceType
     * @param eventId
     * @param os
     * @param language
     * @param eventType
     * @param version
     * @param deviceId
     * @param userId
     * @param platform
     * @param carrier
     * @param deviceModel
     * @param deviceManufacturer
     * @param timestamp
     */
    public Common(String eventId, String os, String version, String language, String carrier, String deviceId, Common.EventType eventType, String userId, Double timestamp, String deviceType, Common.Platform platform, String deviceManufacturer, String deviceModel) {
        super();
        this.eventId = eventId;
        this.os = os;
        this.version = version;
        this.language = language;
        this.carrier = carrier;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.userId = userId;
        this.timestamp = timestamp;
        this.deviceType = deviceType;
        this.platform = platform;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceModel = deviceModel;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getOs() {
        return os;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOs(String os) {
        this.os = os;
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
    public String getLanguage() {
        return language;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
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
    public Common.EventType getEventType() {
        return eventType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventType(Common.EventType eventType) {
        this.eventType = eventType;
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
    public Double getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Common.Platform getPlatform() {
        return platform;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPlatform(Common.Platform platform) {
        this.platform = platform;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getDeviceModel() {
        return deviceModel;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public enum EventType {

        @SerializedName("business")
        BUSINESS("business"),
        @SerializedName("analytics")
        ANALYTICS("analytics"),
        @SerializedName("log")
        LOG("log");
        private final String value;
        private final static Map<String, Common.EventType> CONSTANTS = new HashMap<String, Common.EventType>();

        static {
            for (Common.EventType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private EventType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Common.EventType fromValue(String value) {
            Common.EventType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Platform {

        @SerializedName("iOS")
        I_OS("iOS"),
        @SerializedName("Android")
        ANDROID("Android"),
        @SerializedName("Web")
        WEB("Web");
        private final String value;
        private final static Map<String, Common.Platform> CONSTANTS = new HashMap<String, Common.Platform>();

        static {
            for (Common.Platform c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Platform(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Common.Platform fromValue(String value) {
            Common.Platform constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
