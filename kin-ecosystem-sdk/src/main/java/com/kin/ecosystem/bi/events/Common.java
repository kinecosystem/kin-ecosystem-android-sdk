
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
    @SerializedName("latitude")
    @Expose
    private String latitude;
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
    @SerializedName("city")
    @Expose
    private String city;
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
    @SerializedName("longitude")
    @Expose
    private String longitude;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("country")
    @Expose
    private String country;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("ip_address")
    @Expose
    private String ipAddress;
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
    @SerializedName("region")
    @Expose
    private String region;
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
     * 
     * (Required)
     * 
     */
    @SerializedName("ingest_timestamp")
    @Expose
    private Double ingestTimestamp;

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
     * @param country
     * @param os
     * @param city
     * @param ingestTimestamp
     * @param latitude
     * @param ipAddress
     * @param language
     * @param eventType
     * @param version
     * @param deviceId
     * @param userId
     * @param platform
     * @param carrier
     * @param deviceModel
     * @param region
     * @param deviceManufacturer
     * @param timestamp
     * @param longitude
     */
    public Common(String eventId, String os, String version, String language, String carrier, String deviceId, Common.EventType eventType, String latitude, String userId, Double timestamp, String city, String deviceType, String longitude, String country, String ipAddress, Common.Platform platform, String region, String deviceManufacturer, String deviceModel, Double ingestTimestamp) {
        super();
        this.eventId = eventId;
        this.os = os;
        this.version = version;
        this.language = language;
        this.carrier = carrier;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.latitude = latitude;
        this.userId = userId;
        this.timestamp = timestamp;
        this.city = city;
        this.deviceType = deviceType;
        this.longitude = longitude;
        this.country = country;
        this.ipAddress = ipAddress;
        this.platform = platform;
        this.region = region;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceModel = deviceModel;
        this.ingestTimestamp = ingestTimestamp;
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
    public String getLatitude() {
        return latitude;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
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
    public String getCity() {
        return city;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setCity(String city) {
        this.city = city;
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
    public String getLongitude() {
        return longitude;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
    public String getRegion() {
        return region;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setRegion(String region) {
        this.region = region;
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

    /**
     * 
     * (Required)
     * 
     */
    public Double getIngestTimestamp() {
        return ingestTimestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setIngestTimestamp(Double ingestTimestamp) {
        this.ingestTimestamp = ingestTimestamp;
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
