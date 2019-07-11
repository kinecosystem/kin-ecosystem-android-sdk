package com.kin.ecosystem.common.exception;


import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ServiceException extends KinEcosystemException {

	@IntDef({SERVICE_ERROR, NETWORK_ERROR, TIMEOUT_ERROR, USER_NOT_FOUND, ORDER_FAILED, USER_HAS_NO_WALLET,
		BLOCKCHAIN_ENDPOINT_CHANGED, WALLET_WAS_NOT_CREATED_IN_THIS_APP, MAX_WALLETS_EXCEEDED})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ServiceErrorCodes {

	}

	public static final int SERVICE_ERROR = 5001;
	public static final int NETWORK_ERROR = 5002;
	public static final int TIMEOUT_ERROR = 5003;
	public static final int USER_NOT_FOUND = 5004;
	public static final int ORDER_FAILED = 5005;
	public static final int USER_HAS_NO_WALLET = 5006;
	public static final int BLOCKCHAIN_ENDPOINT_CHANGED = 5007;
	public static final int WALLET_WAS_NOT_CREATED_IN_THIS_APP = 5008;
	public static final int MAX_WALLETS_EXCEEDED = 5009;

	public ServiceException(@ServiceErrorCodes int code, String message, Throwable cause) {
		super(code, message, cause);
	}
}
