package com.kin.ecosystem.network.model;

public enum Origin {

    MARKETPLACE("marketplace"),
    EXTERNAL("origin");

    private String origin;

    Origin(String origin) {
        this.origin = origin;
    }

    public String getValue() {
        return origin;
    }
}
