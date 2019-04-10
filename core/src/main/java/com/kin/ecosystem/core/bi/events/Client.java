
package com.kin.ecosystem.core.bi.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * common properties for all client events
 * 
 */
public class Client implements ClientInterface {

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
    public Client() {
    }

    /**
     * 
     * @param carrier
     * @param os
     * @param language
     * @param deviceModel
     * @param deviceManufacturer
     */
    public Client(String os, String language, String carrier, String deviceManufacturer, String deviceModel) {
        super();
        this.os = os;
        this.language = language;
        this.carrier = carrier;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceModel = deviceModel;
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

}
