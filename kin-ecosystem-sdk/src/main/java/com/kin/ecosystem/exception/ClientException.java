package com.kin.ecosystem.exception;

public class ClientException extends KinEcosystemException {

    public static final int SDK_NOT_STARTED = 4001;
    public static final int BAD_CONFIGURATION = 4002;
    public static final int INTERNAL_INCONSISTENCY = 4003;

    public ClientException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
