package com.kin.ecosystem.exception;

public class KinEcosystemException extends Exception {

    public static final int UNKNOWN = 9999;

    private int code;

    public KinEcosystemException(int code, String message) {
        super(message);
        this.code = code;
    }

    public KinEcosystemException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
