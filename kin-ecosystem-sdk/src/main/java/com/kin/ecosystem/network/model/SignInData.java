package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * payload required for signing in
 */
public class SignInData {
    @SerializedName("jwt")
    private String jwt = null;
    @SerializedName("device_id")
    private String deviceId = null;
    @SerializedName("public_address")
    private String publicAddress = null;

    public SignInData jwt(String jwt) {
        this.jwt = jwt;
        return this;
    }


    /**
     * jwt should contain \&quot;user_id\&quot;, \&quot;app_id\&quot; and \&quot;timestamp\&quot;
     *
     * @return jwt
     **/
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public SignInData deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }


    /**
     * Get deviceId
     *
     * @return deviceId
     **/
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public SignInData publicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
        return this;
    }


    /**
     * the address where earned funds will go to
     *
     * @return publicAddress
     **/
    public String getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignInData signInData = (SignInData) o;
        return Objects.equals(this.jwt, signInData.jwt) &&
                Objects.equals(this.deviceId, signInData.deviceId) &&
                Objects.equals(this.publicAddress, signInData.publicAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwt, deviceId, publicAddress);
    }
}



