package com.kin.ecosystem.backup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class BackupManagerImpl implements BackupManager {

	@Override
	public void backupFlow(@NonNull Activity activity, @NonNull KeyStoreProvider keyStoreProvider,
		@NonNull Callback callback, @Nullable BackupEvents events) {
		new Launcher(activity).backupFlow(keyStoreProvider, callback, events);
	}

	@Override
	public void restoreFlow(@NonNull Activity activity, @NonNull KeyStoreProvider keyStoreProvider,
		@NonNull Callback callback, @Nullable RestoreEvents events) {
		new Launcher(activity).restoreFlow(keyStoreProvider, callback, events);
	}
}
