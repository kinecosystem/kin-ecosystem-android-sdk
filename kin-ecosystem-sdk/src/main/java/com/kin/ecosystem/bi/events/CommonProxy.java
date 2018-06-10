package com.kin.ecosystem.bi.events;

import com.kin.ecosystem.bi.EventsStore;

public class CommonProxy implements CommonInterface {
    public Common snapshot() {
        return new Common(
            this.getEventId(),
            this.getOs(),
            this.getVersion(),
            this.getLanguage(),
            this.getCarrier(),
            this.getDeviceId(),
            this.getUserId(),
            this.getTimestamp(),
            this.getPlatform(),
            this.getDeviceManufacturer(),
            this.getDeviceModel());
    }

    private String eventId;
    private EventsStore.DynamicValue<String> dynamicEventId;
    public String getEventId() {
        return this.eventId != null ? this.eventId : this.dynamicEventId.get();
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
        return this.os != null ? this.os : this.dynamicOs.get();
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
        return this.version != null ? this.version : this.dynamicVersion.get();
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
        return this.language != null ? this.language : this.dynamicLanguage.get();
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
        return this.carrier != null ? this.carrier : this.dynamicCarrier.get();
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
        return this.deviceId != null ? this.deviceId : this.dynamicDeviceId.get();
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public void setDeviceId(EventsStore.DynamicValue<String> deviceId) {
        this.dynamicDeviceId = deviceId;
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

    private Double timestamp;
    private EventsStore.DynamicValue<Double> dynamicTimestamp;
    public Double getTimestamp() {
        return this.timestamp != null ? this.timestamp : this.dynamicTimestamp.get();
    }
    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }
    public void setTimestamp(EventsStore.DynamicValue<Double> timestamp) {
        this.dynamicTimestamp = timestamp;
    }

    private Common.Platform platform;
    private EventsStore.DynamicValue<Common.Platform> dynamicPlatform;
    public Common.Platform getPlatform() {
        return this.platform != null ? this.platform : this.dynamicPlatform.get();
    }
    public void setPlatform(Common.Platform platform) {
        this.platform = platform;
    }
    public void setPlatform(EventsStore.DynamicValue<Common.Platform> platform) {
        this.dynamicPlatform = platform;
    }

    private String deviceManufacturer;
    private EventsStore.DynamicValue<String> dynamicDeviceManufacturer;
    public String getDeviceManufacturer() {
        return this.deviceManufacturer != null ? this.deviceManufacturer : this.dynamicDeviceManufacturer.get();
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
        return this.deviceModel != null ? this.deviceModel : this.dynamicDeviceModel.get();
    }
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    public void setDeviceModel(EventsStore.DynamicValue<String> deviceModel) {
        this.dynamicDeviceModel = deviceModel;
    }

}
