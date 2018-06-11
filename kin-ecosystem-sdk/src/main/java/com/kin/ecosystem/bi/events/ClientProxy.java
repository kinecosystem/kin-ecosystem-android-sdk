package com.kin.ecosystem.bi.events;

import com.kin.ecosystem.bi.EventsStore;

public class ClientProxy implements ClientInterface {
    public Client snapshot() {
        return new Client(
            this.getOs(),
            this.getLanguage(),
            this.getCarrier(),
            this.getDeviceId(),
            this.getDeviceManufacturer(),
            this.getDeviceModel());
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
