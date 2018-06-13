package com.kin.ecosystem.exception;

import com.kin.ecosystem.network.ApiException;

public class ClientException extends KinEcosystemException {

    public static final int SDK_NOT_STARTED = 1001;
    public static final int BAD_CONFIGURATION = 1002;
    public static final int INTERNAL_INCONSISTENCY = 1003;

    public ClientException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
