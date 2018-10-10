package com.kin.ecosystem.backup.exception;

public class BackupException extends Exception {

	public static final int CODE_UNEXPECTED = 501;

	private final int code;

	public BackupException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
