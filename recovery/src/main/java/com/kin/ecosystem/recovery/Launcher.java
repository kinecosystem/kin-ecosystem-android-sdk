package com.kin.ecosystem.recovery;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.restore.view.RestoreActivity;

class Launcher {

	private final Activity activity;

	Launcher(@NonNull final Activity activity) {
		this.activity = activity;
	}

	void backupFlow(@NonNull final KeyStoreProvider keyStoreProvider) {
		//TODO start backup flow, should do startActivityForResult
	}

	void restoreFlow(@NonNull final KeyStoreProvider keyStoreProvider) {
		activity.startActivityForResult(new Intent(activity, RestoreActivity.class), CallbackManager.REQ_CODE_RESTORE);
	}
}
