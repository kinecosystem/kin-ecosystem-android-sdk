package com.kin.ecosystem.backup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class Launcher {

	private final Activity activity;

	Launcher(@NonNull final Activity activity) {
		this.activity = activity;
	}

	public void backupFlow(@NonNull final KeyStoreProvider keyStoreProvider, @NonNull final Callback callback,
		@Nullable final BackupEvents events) {
		//TODO start backup flow
	}

	public void restoreFlow(@NonNull final KeyStoreProvider keyStoreProvider, @NonNull final Callback callback,
		@Nullable final RestoreEvents events) {
		//TODO start restore flow
	}
}
