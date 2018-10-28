package com.kin.ecosystem.recovery.events;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface BroadcastManager {

	interface Listener {

		void onReceive(Intent data);

	}
	void register(@NonNull final Listener listener);

	void unregister();

	void sendEvent(Intent data);

	void sendCallback(int resultCode, Intent data);

}
