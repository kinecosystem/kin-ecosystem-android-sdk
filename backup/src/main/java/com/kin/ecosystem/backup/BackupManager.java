package com.kin.ecosystem.backup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public final class BackupManager {

	private static volatile KeyStoreProvider keyStoreProvider;
	private final CallbackManager callbackManager;
	private Activity activity;

	public BackupManager(@NonNull final Activity activity, @NonNull final KeyStoreProvider keyStoreProvider) {
		Validator.checkNotNull(activity, "activity");
		BackupManager.keyStoreProvider = keyStoreProvider;
		this.activity = activity;
		final Context applicationContext = activity.getApplicationContext();
		this.callbackManager = new CallbackManager(
			new EventDispatcherImpl(new BroadcastManagerImpl(applicationContext)));
	}

	protected static KeyStoreProvider getKeyStoreProvider() {
		return keyStoreProvider;
	}

	public void backupFlow() {
		new Launcher(activity).backupFlow(keyStoreProvider);
	}

	public void restoreFlow() {
		new Launcher(activity).restoreFlow(keyStoreProvider);
	}

	public void registerBackupCallback(@NonNull final BackupCallback backupCallback) {
		Validator.checkNotNull(backupCallback, "backupCallback");
		this.callbackManager.setBackupCallback(backupCallback);
	}

	public void registerBackupEvents(@NonNull final BackupEvents backupEvents) {
		Validator.checkNotNull(backupEvents, "backupEvents");
		this.callbackManager.setBackupEvents(backupEvents);
	}

	public void registerRestoreCallback(@NonNull final RestoreCallback restoreCallback) {
		Validator.checkNotNull(restoreCallback, "restoreCallback");
		this.callbackManager.setRestoreCallback(restoreCallback);
	}

	public void registerRestoreEvents(@NonNull final RestoreEvents restoreEvents) {
		Validator.checkNotNull(restoreEvents, "restoreEvents");
		this.callbackManager.setRestoreEvents(restoreEvents);
	}

	public void release() {
		this.activity = null;
		this.callbackManager.unregisterCallbacksAndEvents();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.callbackManager.onActivityResult(requestCode, resultCode, data);
	}
}
