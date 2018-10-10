package com.kin.ecosystem.backup;

import android.content.Intent;
import android.support.annotation.NonNull;

interface BroadcastManager {

	interface Listener {

		void onReceive(Intent data);
	}

	void register(@NonNull final Listener listener);

	void unregister();

	void sendEvent(Intent data);

}
