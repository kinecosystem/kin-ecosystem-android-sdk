package com.kin.ecosystem.backup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.backup.BroadcastManager.Listener;

class EventDispatcherImpl implements EventDispatcher {

	private @Nullable
	BackupEvents backupEvents;
	private @Nullable
	RestoreEvents restoreEvents;

	private @NonNull
	final BroadcastManager broadcastManager;

	public EventDispatcherImpl(@NonNull final BroadcastManager broadcastManager) {
		this.broadcastManager = broadcastManager;
	}

	@Override
	public void setBackupEvents(@Nullable BackupEvents backupEvents) {
		this.backupEvents = backupEvents;
		if (backupEvents != null) {
			registerBroadcastListener();
		}
	}

	@Override
	public void setRestoreEvents(@Nullable RestoreEvents restoreEvents) {
		this.restoreEvents = restoreEvents;
		if (restoreEvents != null) {
			registerBroadcastListener();
		}
	}

	@Override
	public void sendEvent(@EventType final int eventType, @NonNull String eventName) {
		Intent data = new Intent();
		data.putExtra(EXTRA_KEY_EVENT_TYPE, eventType);
		data.putExtra(EXTRA_KEY_EVENT_NAME, eventName);
		broadcastManager.sendEvent(data);
	}

	private void registerBroadcastListener() {
		broadcastManager.register(new Listener() {
			@Override
			public void onReceive(Intent data) {
				parseData(data);
			}
		});
	}

	private void parseData(Intent data) {
		//TODO parse data and send events
	}

}
