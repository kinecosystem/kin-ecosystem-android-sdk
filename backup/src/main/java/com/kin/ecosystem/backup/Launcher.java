package com.kin.ecosystem.backup;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.backup.restore.view.RestoreActivity;

class Launcher {

	private final Activity activity;

	Launcher(@NonNull final Activity activity) {
		this.activity = activity;
	}

	void backupFlow(@NonNull final KeyStoreProvider keyStoreProvider) {
		//TODO start backup flow, should do startActivityForResult
	}

	void restoreFlow(@NonNull final KeyStoreProvider keyStoreProvider) {
		activity.startActivity(new Intent(activity, RestoreActivity.class));
	}
}
