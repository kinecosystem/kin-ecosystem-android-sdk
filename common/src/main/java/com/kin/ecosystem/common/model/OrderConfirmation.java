package com.kin.ecosystem.common.model;

import com.kin.ecosystem.common.exception.KinEcosystemException;

public class OrderConfirmation {

    public enum Status {

        PENDING("pending"),
        COMPLETED("completed"),
        FAILED("failed");

        private String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static Status fromValue(String text) {
            for (Status b : Status.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private Status status;

    private String jwtConfirmation;

    private KinEcosystemException exception;

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

    public KinEcosystemException getException() {
        return exception;
    }

    public void setException(KinEcosystemException exception) {
        this.exception = exception;
    }
}
