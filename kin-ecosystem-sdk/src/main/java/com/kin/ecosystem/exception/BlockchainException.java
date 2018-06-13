package com.kin.ecosystem.exception;

public class BlockchainException extends KinEcosystemException {

	public static final int ACCOUNT_CREATION_FAILED = 6001;
	public static final int ACCOUNT_NOT_FOUND = 6002;
	public static final int ACCOUNT_ACTIVATION_FAILED = 6003;
	public static final int INSUFFICIENT_KIN = 6004;
	public static final int TRANSACTION_FAILED = 6005;

	public BlockchainException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}
}
