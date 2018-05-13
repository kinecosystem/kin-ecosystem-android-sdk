package com.kin.ecosystem.data.model;

import com.kin.ecosystem.network.model.Order.Status;

public class OrderConfirmation {

    private Status status;

    private String jwtConfirmation;

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setJwtConfirmation(String jwtConfirmation) {
        this.jwtConfirmation = jwtConfirmation;
    }

    public Status getStatus() {
        return status;
    }

    public String getJwtConfirmation() {
        return jwtConfirmation;
    }
}
