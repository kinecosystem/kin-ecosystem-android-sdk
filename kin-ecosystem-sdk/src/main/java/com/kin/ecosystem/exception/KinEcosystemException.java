package com.kin.ecosystem.exception;

import android.support.annotation.NonNull;

public class KinEcosystemException extends Exception {

	public static final int UNKNOWN = 9999;

	private int code;

	public KinEcosystemException(@NonNull int code, @NonNull String message, @NonNull Throwable cause) {
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
