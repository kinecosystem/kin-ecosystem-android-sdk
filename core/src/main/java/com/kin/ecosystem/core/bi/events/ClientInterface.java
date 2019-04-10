package com.kin.ecosystem.core.bi.events;

public interface ClientInterface extends ClientReadonly {
    void setOs(String os);

    void setLanguage(String language);

    void setCarrier(String carrier);

    void setDeviceManufacturer(String deviceManufacturer);

    void setDeviceModel(String deviceModel);

}
