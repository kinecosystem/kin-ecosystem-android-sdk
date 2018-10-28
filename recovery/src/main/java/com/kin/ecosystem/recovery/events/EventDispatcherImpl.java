package com.kin.ecosystem.recovery.events;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.recovery.BackupEvents;
import com.kin.ecosystem.recovery.RestoreEvents;
import com.kin.ecosystem.recovery.events.BroadcastManager.Listener;

public class EventDispatcherImpl implements EventDispatcher {

	@Nullable
	private BackupEvents backupEvents;
	@Nullable
	private RestoreEvents restoreEvents;

	@NonNull
	private final BroadcastManager broadcastManager;
	private Listener broadcastListener;

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

	@Override
	public void sendCallback(int resultCode, Intent data) {
		broadcastManager.sendCallback(resultCode, data);
	}

	@Override
	public void unregister() {
		if (broadcastListener != null) {
			broadcastManager.unregister();
		}
	}

	private void registerBroadcastListener() {
		if (broadcastListener != null) {
			broadcastListener = new Listener() {
				@Override
				public void onReceive(Intent data) {
					parseData(data);

				}
			};
			broadcastManager.register(broadcastListener);
		}
	}

	private void parseData(Intent data) {
		//TODO parse data and send events
	}

}
