package com.kin.ecosystem.backup;

import android.app.Activity;
import android.support.annotation.NonNull;

class Launcher {

	private final Activity activity;

	Launcher(@NonNull final Activity activity) {
		this.activity = activity;
	}

	public void backupFlow(@NonNull final KeyStoreProvider keyStoreProvider) {
		//TODO start backup flow, should do startActivityForResult
	}

	public void restoreFlow(@NonNull final KeyStoreProvider keyStoreProvider) {
		//TODO start restore flow, should do startActivityForResult
	}
}
