package com.kin.ecosystem.exception;

public class ServiceException extends KinEcosystemException {

    public static final int SERVICE_ERROR = 5001;
    public static final int NETWORK_ERROR = 5002;
    public static final int TIMEOUT_ERROR = 5003;

    public ServiceException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
