package com.kin.ecosystem.recovery.exception;

public class BackupException extends Exception {

	public static final int CODE_UNEXPECTED = 501;

	public static final int CODE_BACKUP_FAILED = 100;
	public static final int CODE_RESTORE_FAILED = 101;
	public static final int CODE_RESTORE_INVALID_KEYSTORE_FORMAT = 102;


	private final int code;

	public BackupException(int code, String message) {
		super(message);
		this.code = code;
	}

	public BackupException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
