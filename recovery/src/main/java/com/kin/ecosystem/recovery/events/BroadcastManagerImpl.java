package com.kin.ecosystem.recovery.events;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.content.LocalBroadcastManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BroadcastManagerImpl implements BroadcastManager {

	private final Activity activity;
	private Listener listener;

	private BroadcastReceiver receiver;



	static final String ACTION_EVENTS_BACKUP = "ACTION_EVENTS_BACKUP";
	static final String ACTION_EVENTS_RESTORE = "ACTION_EVENTS_RESTORE";

	@StringDef({ACTION_EVENTS_BACKUP, ACTION_EVENTS_RESTORE})
	@Retention(RetentionPolicy.SOURCE)
	@interface ActionName {

	}

	public BroadcastManagerImpl(@NonNull Activity activity) {
		this.activity = activity;
	}

	@Override
	public void register(@NonNull final Listener listener, @ActionName final String actionName) {
		this.listener = listener;
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent data) {
				BroadcastManagerImpl.this.listener.onReceive(data);
			}
		};
		LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, new IntentFilter(actionName));
	}

	@Override
	public void unregister() {
		if (receiver != null) {
			LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
		}
	}

	@Override
	public void sendEvent(Intent data, @ActionName final String actionName) {
		data.setAction(actionName);
		LocalBroadcastManager.getInstance(activity).sendBroadcast(data);
	}

	@Override
	public void sendCallback(int resultCode, Intent data) {
		activity.setResult(resultCode, data);
	}
}
