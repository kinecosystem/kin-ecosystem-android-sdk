package com.kin.ecosystem.recovery.events;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl.ActionName;

public interface BroadcastManager {

	interface Listener {

		void onReceive(Intent data);
	}

	void register(@NonNull final Listener listener, @ActionName final String actionName);

	void unregisterAll();

	void sendEvent(Intent data, @ActionName final String actionName);

	void setActivityResult(int resultCode, Intent data);

}
