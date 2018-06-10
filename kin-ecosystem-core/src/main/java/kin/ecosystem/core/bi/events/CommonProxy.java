package kin.ecosystem.core.bi.events;

import kin.ecosystem.core.bi.EventsStore;

public class CommonProxy implements CommonInterface {
    public Common snapshot() {
        return new Common(
            this.getEventId(),
            this.getOs(),
            this.getVersion(),
            this.getLanguage(),
            this.getCarrier(),
            this.getDeviceId(),
            this.getEventType(),
            this.getLatitude(),
            this.getUserId(),
            this.getTimestamp(),
            this.getCity(),
            this.getDeviceType(),
            this.getLongitude(),
            this.getCountry(),
            this.getIpAddress(),
            this.getPlatform(),
            this.getRegion(),
            this.getDeviceManufacturer(),
            this.getDeviceModel(),
            this.getIngestTimestamp());
    }

    private String eventId;
    private EventsStore.DynamicValue<String> dynamicEventId;
    public String getEventId() {
        return this.eventId == null ? this.eventId : this.dynamicEventId.get();
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public void setEventId(EventsStore.DynamicValue<String> eventId) {
        this.dynamicEventId = eventId;
    }

    private String os;
    private EventsStore.DynamicValue<String> dynamicOs;
    public String getOs() {
        return this.os == null ? this.os : this.dynamicOs.get();
    }
    public void setOs(String os) {
        this.os = os;
    }
    public void setOs(EventsStore.DynamicValue<String> os) {
        this.dynamicOs = os;
    }

    private String version;
    private EventsStore.DynamicValue<String> dynamicVersion;
    public String getVersion() {
        return this.version == null ? this.version : this.dynamicVersion.get();
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public void setVersion(EventsStore.DynamicValue<String> version) {
        this.dynamicVersion = version;
    }

    private String language;
    private EventsStore.DynamicValue<String> dynamicLanguage;
    public String getLanguage() {
        return this.language == null ? this.language : this.dynamicLanguage.get();
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public void setLanguage(EventsStore.DynamicValue<String> language) {
        this.dynamicLanguage = language;
    }

    private String carrier;
    private EventsStore.DynamicValue<String> dynamicCarrier;
    public String getCarrier() {
        return this.carrier == null ? this.carrier : this.dynamicCarrier.get();
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    public void setCarrier(EventsStore.DynamicValue<String> carrier) {
        this.dynamicCarrier = carrier;
    }

    private String deviceId;
    private EventsStore.DynamicValue<String> dynamicDeviceId;
    public String getDeviceId() {
        return this.deviceId == null ? this.deviceId : this.dynamicDeviceId.get();
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public void setDeviceId(EventsStore.DynamicValue<String> deviceId) {
        this.dynamicDeviceId = deviceId;
    }

    private Common.EventType eventType;
    private EventsStore.DynamicValue<Common.EventType> dynamicEventType;
    public Common.EventType getEventType() {
        return this.eventType == null ? this.eventType : this.dynamicEventType.get();
    }
    public void setEventType(Common.EventType eventType) {
        this.eventType = eventType;
    }
    public void setEventType(EventsStore.DynamicValue<Common.EventType> eventType) {
        this.dynamicEventType = eventType;
    }

    private String latitude;
    private EventsStore.DynamicValue<String> dynamicLatitude;
    public String getLatitude() {
        return this.latitude == null ? this.latitude : this.dynamicLatitude.get();
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setLatitude(EventsStore.DynamicValue<String> latitude) {
        this.dynamicLatitude = latitude;
    }

    private String userId;
    private EventsStore.DynamicValue<String> dynamicUserId;
    public String getUserId() {
        return this.userId == null ? this.userId : this.dynamicUserId.get();
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserId(EventsStore.DynamicValue<String> userId) {
        this.dynamicUserId = userId;
    }

    private Double timestamp;
    private EventsStore.DynamicValue<Double> dynamicTimestamp;
    public Double getTimestamp() {
        return this.timestamp == null ? this.timestamp : this.dynamicTimestamp.get();
    }
    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }
    public void setTimestamp(EventsStore.DynamicValue<Double> timestamp) {
        this.dynamicTimestamp = timestamp;
    }

    private String city;
    private EventsStore.DynamicValue<String> dynamicCity;
    public String getCity() {
        return this.city == null ? this.city : this.dynamicCity.get();
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setCity(EventsStore.DynamicValue<String> city) {
        this.dynamicCity = city;
    }

    private String deviceType;
    private EventsStore.DynamicValue<String> dynamicDeviceType;
    public String getDeviceType() {
        return this.deviceType == null ? this.deviceType : this.dynamicDeviceType.get();
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public void setDeviceType(EventsStore.DynamicValue<String> deviceType) {
        this.dynamicDeviceType = deviceType;
    }

    private String longitude;
    private EventsStore.DynamicValue<String> dynamicLongitude;
    public String getLongitude() {
        return this.longitude == null ? this.longitude : this.dynamicLongitude.get();
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public void setLongitude(EventsStore.DynamicValue<String> longitude) {
        this.dynamicLongitude = longitude;
    }

    private String country;
    private EventsStore.DynamicValue<String> dynamicCountry;
    public String getCountry() {
        return this.country == null ? this.country : this.dynamicCountry.get();
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setCountry(EventsStore.DynamicValue<String> country) {
        this.dynamicCountry = country;
    }

    private String ipAddress;
    private EventsStore.DynamicValue<String> dynamicIpAddress;
    public String getIpAddress() {
        return this.ipAddress == null ? this.ipAddress : this.dynamicIpAddress.get();
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setIpAddress(EventsStore.DynamicValue<String> ipAddress) {
        this.dynamicIpAddress = ipAddress;
    }

    private Common.Platform platform;
    private EventsStore.DynamicValue<Common.Platform> dynamicPlatform;
    public Common.Platform getPlatform() {
        return this.platform == null ? this.platform : this.dynamicPlatform.get();
    }
    public void setPlatform(Common.Platform platform) {
        this.platform = platform;
    }
    public void setPlatform(EventsStore.DynamicValue<Common.Platform> platform) {
        this.dynamicPlatform = platform;
    }

    private String region;
    private EventsStore.DynamicValue<String> dynamicRegion;
    public String getRegion() {
        return this.region == null ? this.region : this.dynamicRegion.get();
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public void setRegion(EventsStore.DynamicValue<String> region) {
        this.dynamicRegion = region;
    }

    private String deviceManufacturer;
    private EventsStore.DynamicValue<String> dynamicDeviceManufacturer;
    public String getDeviceManufacturer() {
        return this.deviceManufacturer == null ? this.deviceManufacturer : this.dynamicDeviceManufacturer.get();
    }
    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }
    public void setDeviceManufacturer(EventsStore.DynamicValue<String> deviceManufacturer) {
        this.dynamicDeviceManufacturer = deviceManufacturer;
    }

    private String deviceModel;
    private EventsStore.DynamicValue<String> dynamicDeviceModel;
    public String getDeviceModel() {
        return this.deviceModel == null ? this.deviceModel : this.dynamicDeviceModel.get();
    }
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    public void setDeviceModel(EventsStore.DynamicValue<String> deviceModel) {
        this.dynamicDeviceModel = deviceModel;
    }

    private Double ingestTimestamp;
    private EventsStore.DynamicValue<Double> dynamicIngestTimestamp;
    public Double getIngestTimestamp() {
        return this.ingestTimestamp == null ? this.ingestTimestamp : this.dynamicIngestTimestamp.get();
    }
    public void setIngestTimestamp(Double ingestTimestamp) {
        this.ingestTimestamp = ingestTimestamp;
    }
    public void setIngestTimestamp(EventsStore.DynamicValue<Double> ingestTimestamp) {
        this.dynamicIngestTimestamp = ingestTimestamp;
    }

}
