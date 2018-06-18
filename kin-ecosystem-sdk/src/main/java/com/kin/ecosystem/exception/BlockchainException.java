package com.kin.ecosystem.exception;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BlockchainException extends KinEcosystemException {

	@IntDef({ACCOUNT_CREATION_FAILED, ACCOUNT_NOT_FOUND,
		ACCOUNT_ACTIVATION_FAILED, INSUFFICIENT_KIN,
		TRANSACTION_FAILED, UNKNOWN})
	@Retention(RetentionPolicy.SOURCE)
	public @interface BlockchainErrorCodes {

	}

	public static final int ACCOUNT_CREATION_FAILED = 6001;
	public static final int ACCOUNT_NOT_FOUND = 6002;
	public static final int ACCOUNT_ACTIVATION_FAILED = 6003;
	public static final int INSUFFICIENT_KIN = 6004;
	public static final int TRANSACTION_FAILED = 6005;

	public BlockchainException(@BlockchainErrorCodes int code, String message, Throwable cause) {
		super(code, message, cause);
	}
}
