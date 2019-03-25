package com.kin.ecosystem.common.exception;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ClientException extends KinEcosystemException {

	@IntDef({SDK_NOT_STARTED, BAD_CONFIGURATION,
		INTERNAL_INCONSISTENCY, ACCOUNT_NOT_LOGGED_IN})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ClientErrorCodes {

	}

	public static final int SDK_NOT_STARTED = 4001;
	public static final int BAD_CONFIGURATION = 4002;
	public static final int INTERNAL_INCONSISTENCY = 4003;
	public static final int ACCOUNT_NOT_LOGGED_IN = 4004;
	public static final int BLOCKCHAIN_ENDPOINT_CHANGED = 4101;

	public ClientException(@ClientErrorCodes int code, String message, Throwable cause) {
		super(code, message, cause);
	}
}
