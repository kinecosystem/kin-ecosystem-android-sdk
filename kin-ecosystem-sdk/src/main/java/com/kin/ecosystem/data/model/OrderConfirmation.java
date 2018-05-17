package com.kin.ecosystem.data.model;

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
