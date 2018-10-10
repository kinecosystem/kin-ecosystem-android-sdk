package com.kin.ecosystem.backup;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface EventDispatcher {


	@IntDef({BACKUP_EVENTS, RESTORE_EVENTS})
	@Retention(RetentionPolicy.SOURCE)
	public @interface EventType {

	}

	int BACKUP_EVENTS = 0x00000001;
	int RESTORE_EVENTS = 0x00000002;

	String EXTRA_KEY_EVENT_TYPE = "EVENT_TYPE";
	String EXTRA_KEY_EVENT_NAME = "EVENT_NAME";

	void setBackupEvents(@Nullable BackupEvents backupEvents);

	void setRestoreEvents(@Nullable RestoreEvents restoreEvents);

	void sendEvent(@EventType final int eventType, @NonNull final String eventName);

	void unregister();
}
