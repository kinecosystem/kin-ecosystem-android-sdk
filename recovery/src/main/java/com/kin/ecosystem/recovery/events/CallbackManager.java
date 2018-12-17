package com.kin.ecosystem.recovery.events;

import static com.kin.ecosystem.recovery.exception.BackupException.CODE_UNEXPECTED;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.recovery.BackupCallback;
import com.kin.ecosystem.recovery.BackupEvents;
import com.kin.ecosystem.recovery.RestoreCallback;
import com.kin.ecosystem.recovery.RestoreEvents;
import com.kin.ecosystem.recovery.exception.BackupException;

public class CallbackManager {

	@Nullable
	private BackupCallback backupCallback;
	@Nullable
	private RestoreCallback restoreCallback;

	private EventDispatcher eventDispatcher;

	// Request Code
	public static final int REQ_CODE_BACKUP = 9000;
	public static final int REQ_CODE_RESTORE = 9001;

	// Result Code
	static final int RES_CODE_SUCCESS = 5000;
	static final int RES_CODE_CANCEL = 5001;
	static final int RES_CODE_FAILED = 5002;
	static final String EXTRA_KEY_ERROR_MESSAGE = "EXTRA_KEY_ERROR_MESSAGE";
	static final String EXTRA_KEY_ERROR_CODE = "EXTRA_KEY_ERROR_CODE";
	static final String EXTRA_KEY_IMPORTED_ACCOUNT_INDEX = "EXTRA_KEY_IMPORTED_ACCOUNT_INDEX";

	public CallbackManager(@NonNull final EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public void setBackupCallback(@Nullable BackupCallback backupCallback) {
		this.backupCallback = backupCallback;
	}

	public void setRestoreCallback(@Nullable RestoreCallback restoreCallback) {
		this.restoreCallback = restoreCallback;
	}

	public void setBackupEvents(@Nullable BackupEvents backupEvents) {
		if(eventDispatcher != null) {
			this.eventDispatcher.setBackupEvents(backupEvents);
		}
	}

	public void setRestoreEvents(@Nullable RestoreEvents restoreEvents) {
		if(eventDispatcher != null) {
			this.eventDispatcher.setRestoreEvents(restoreEvents);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CODE_BACKUP) {
			handleBackupResult(resultCode, data);
		} else if (requestCode == REQ_CODE_RESTORE) {
			handleRestoreResult(resultCode, data);
		}
	}

	public void sendRestoreSuccessResult(int accountIndex) {
		if(eventDispatcher != null) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_KEY_IMPORTED_ACCOUNT_INDEX, accountIndex);
			eventDispatcher.sendCallback(RES_CODE_SUCCESS, intent);
		}
	}

	public void sendBackupSuccessResult() {
		if(eventDispatcher != null) {
			eventDispatcher.sendCallback(RES_CODE_SUCCESS, null);
		}
	}

	public void sendCancelledResult() {
		if(eventDispatcher != null) {
			eventDispatcher.sendCallback(RES_CODE_CANCEL, null);
		}
	}

	private void handleRestoreResult(int resultCode, Intent data) {
		if (restoreCallback != null) {
			switch (resultCode) {
				case RES_CODE_SUCCESS:
					final int notFoundIndex = -1;
					final int importedAccountIndex = data
						.getIntExtra(EXTRA_KEY_IMPORTED_ACCOUNT_INDEX, notFoundIndex);
					if (importedAccountIndex == notFoundIndex) {
						restoreCallback.onFailure(new BackupException(CODE_UNEXPECTED,
							"Unexpected error - imported account index not found"));
					}
					restoreCallback.onSuccess(importedAccountIndex);
					break;
				case RES_CODE_CANCEL:
					restoreCallback.onCancel();
					break;
				case RES_CODE_FAILED:
					String errorMessage = data.getStringExtra(EXTRA_KEY_ERROR_MESSAGE);
					int code = data.getIntExtra(EXTRA_KEY_ERROR_CODE, 0);
					restoreCallback.onFailure(new BackupException(code, errorMessage));
					break;
				default:
					restoreCallback.onFailure(
						new BackupException(CODE_UNEXPECTED, "Unexpected error - unknown result code " + resultCode));
					break;
			}
		}
	}

	private void handleBackupResult(int resultCode, Intent data) {
		if (backupCallback != null) {
			switch (resultCode) {
				case RES_CODE_SUCCESS:
					backupCallback.onSuccess();
					break;
				case RES_CODE_CANCEL:
					backupCallback.onCancel();
					break;
				case RES_CODE_FAILED:
					String errorMessage = data.getStringExtra(EXTRA_KEY_ERROR_MESSAGE);
					int code = data.getIntExtra(EXTRA_KEY_ERROR_CODE, 0);
					backupCallback.onFailure(new BackupException(code, errorMessage));
					break;
				default:
					backupCallback.onFailure(
						new BackupException(CODE_UNEXPECTED, "Unexpected error - unknown result code " + resultCode));
					break;
			}
		}
	}

	public void sendBackupEvent(@BackupEventCode int eventCode) {
		if(eventDispatcher != null) {
			eventDispatcher.sendEvent(EventDispatcher.BACKUP_EVENTS, eventCode);
		}
	}

	public void sendRestoreEvent(@RestoreEventCode int eventCode) {
		if(eventDispatcher != null) {
			eventDispatcher.sendEvent(EventDispatcher.RESTORE_EVENTS, eventCode);
		}
	}

	public void release() {
		unregisterCallbacksAndEvents();
		if(eventDispatcher != null) {
			eventDispatcher.release();
			eventDispatcher = null;
		}
	}

	private void unregisterCallbacksAndEvents() {
		if(eventDispatcher != null) {
			this.eventDispatcher.unregister();
		}
		this.backupCallback = null;
		this.restoreCallback = null;
	}
}
