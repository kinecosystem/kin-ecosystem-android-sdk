package com.kin.ecosystem.recovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public final class BackupManager {

	private static volatile KeyStoreProvider keyStoreProvider;
	private final CallbackManager callbackManager;

	public BackupManager(@NonNull final Context context, @NonNull final KeyStoreProvider keyStoreProvider) {
		BackupManager.keyStoreProvider = keyStoreProvider;
		final Context applicationContext = context.getApplicationContext();
		this.callbackManager = new CallbackManager(
			new EventDispatcherImpl(new BroadcastManagerImpl(applicationContext)));
	}

	protected static KeyStoreProvider getKeyStoreProvider() {
		return keyStoreProvider;
	}

	public void backupFlow(@NonNull final Activity activity) {
		new Launcher(activity).backupFlow(keyStoreProvider);
	}

	public void restoreFlow(@NonNull final Activity activity) {
		new Launcher(activity).restoreFlow(keyStoreProvider);
	}

	public void registerBackupCallback(@NonNull final BackupCallback backupCallback) {
		this.callbackManager.setBackupCallback(backupCallback);
	}

	public void registerBackupEvents(@NonNull final BackupEvents backupEvents) {
		this.callbackManager.setBackupEvents(backupEvents);
	}

	public void registerRestoreCallback(@NonNull final RestoreCallback restoreCallback) {
		this.callbackManager.setRestoreCallback(restoreCallback);
	}

	public void registerRestoreEvents(@NonNull final RestoreEvents restoreEvents) {
		this.callbackManager.setRestoreEvents(restoreEvents);
	}

	public void unregisterCallbacksAndEvents() {
		this.callbackManager.unregisterCallbacksAndEvents();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.callbackManager.onActivityResult(requestCode, resultCode, data);
	}
}
