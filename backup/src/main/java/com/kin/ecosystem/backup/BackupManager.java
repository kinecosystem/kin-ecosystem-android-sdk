package com.kin.ecosystem.backup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface BackupManager {

	void backupFlow(@NonNull final Activity activity, @NonNull final KeyStoreProvider keyStoreProvider,
		@NonNull final Callback callback, @Nullable final BackupEvents events);

	void restoreFlow(@NonNull final Activity activity, @NonNull final KeyStoreProvider keyStoreProvider,
		@NonNull final Callback callback, @Nullable final RestoreEvents events);

}
