package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;


/**
 * token issued by marketplace server
 */
public class AuthToken {
    @SerializedName("token")
    private String token = null;
    @SerializedName("activated")
    private Boolean activated = null;

    public AuthToken token(String token) {
        this.token = token;
        return this;
    }


    /**
     * Get token
     *
     * @return token
     **/
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthToken activated(Boolean activated) {
        this.activated = activated;
        return this;
    }


    /**
     * Get activated
     *
     * @return activated
     **/
    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(this.token, authToken.token) &&
                Objects.equals(this.activated, authToken.activated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, activated);
    }
}



