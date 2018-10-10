package com.kin.ecosystem.backup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

class BroadcastManagerImpl implements BroadcastManager {

	private final Context context;
	private Listener listener;

	private BroadcastReceiver receiver;

	private static final String ACTION_EVENTS_BACKUP = "ACTION_EVENTS_BACKUP";

	BroadcastManagerImpl(Context context) {
		this.context = context;
	}

	@Override
	public void register(@NonNull final Listener listener) {
		this.listener = listener;
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent data) {
				BroadcastManagerImpl.this.listener.onReceive(data);
			}
		};
		LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(ACTION_EVENTS_BACKUP));
	}

	@Override
	public void unregister() {
		if (receiver != null) {
			LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
		}
	}

	@Override
	public void sendEvent(Intent data) {
		data.setAction(ACTION_EVENTS_BACKUP);
		LocalBroadcastManager.getInstance(context).sendBroadcast(data);
	}
}
