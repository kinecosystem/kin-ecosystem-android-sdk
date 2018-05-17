package com.kin.ecosystem.data.model;

import com.kin.ecosystem.network.model.Order.StatusEnum;

public class OrderConfirmation {

    private StatusEnum status;

    private String jwtConfirmation;

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public void setJwtConfirmation(String jwtConfirmation) {
        this.jwtConfirmation = jwtConfirmation;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public String getJwtConfirmation() {
        return jwtConfirmation;
    }
}
