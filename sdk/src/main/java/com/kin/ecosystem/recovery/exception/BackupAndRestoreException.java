package com.kin.ecosystem.recovery.exception;

import android.support.annotation.IntDef;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BackupAndRestoreException extends KinEcosystemException {

	@IntDef({BACKUP_FAILED, RESTORE_FAILED, RESTORE_INVALID_KEYSTORE_FORMAT, RESTORE_SWITCH_ACCOUNT_FAILED, UNKNOWN})
	@Retention(RetentionPolicy.SOURCE)
	public @interface BackupRestoreErrorCodes {

	}

	public static final int BACKUP_FAILED = 1001;
	public static final int RESTORE_FAILED = 1002;
	public static final int RESTORE_INVALID_KEYSTORE_FORMAT = 1003;
	public static final int RESTORE_SWITCH_ACCOUNT_FAILED = 1004;
	public static final int UNKNOWN = 1005;


	public BackupAndRestoreException(@BackupAndRestoreException.BackupRestoreErrorCodes int code, String message,
		Throwable cause) {
		super(code, message, cause);
	}
}
