package com.kin.ecosystem.recovery;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;

public final class BackupManager {

	private static volatile KeyStoreProvider keyStoreProvider;
	private final CallbackManager callbackManager;
	private Activity activity;

	public BackupManager(@NonNull final Activity activity, @NonNull final KeyStoreProvider keyStoreProvider) {
		Validator.checkNotNull(activity, "activity");
		BackupManager.keyStoreProvider = keyStoreProvider;
		this.activity = activity;
		this.callbackManager = new CallbackManager(
			new EventDispatcherImpl(new BroadcastManagerImpl(activity)));
		if(!AppCompatDelegate.isCompatVectorFromResourcesEnabled()) {
			AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
		}
	}

	public static KeyStoreProvider getKeyStoreProvider() {
		return keyStoreProvider;
	}

	public void backupFlow(KinRecoveryTheme kinRecoveryTheme) {
		new Launcher(activity).backupFlow(kinRecoveryTheme);
	}

	public void restoreFlow(KinRecoveryTheme kinRecoveryTheme) {
		new Launcher(activity).restoreFlow(kinRecoveryTheme);
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
		this.callbackManager.unregisterCallbacksAndEvents();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.callbackManager.onActivityResult(requestCode, resultCode, data);
	}
}
