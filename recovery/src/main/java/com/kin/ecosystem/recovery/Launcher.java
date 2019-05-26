package com.kin.ecosystem.recovery;


import static com.kin.ecosystem.recovery.events.CallbackManager.REQ_CODE_BACKUP;
import static com.kin.ecosystem.recovery.events.CallbackManager.REQ_CODE_RESTORE;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.backup.view.BackupActivity;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.restore.view.RestoreActivity;

class Launcher {

	private final Activity activity;

	Launcher(@NonNull final Activity activity) {
		this.activity = activity;
	}

	void backupFlow(KinRecoveryTheme kinRecoveryTheme) {
		Intent backupIntent = new Intent(activity, BackupActivity.class);
		backupIntent.putExtra(BaseToolbarActivity.KEY_THEME, kinRecoveryTheme.name());
		startForResult(backupIntent, REQ_CODE_BACKUP);
	}

	void restoreFlow(KinRecoveryTheme kinRecoveryTheme) {
		Intent restoreIntent = new Intent(activity, RestoreActivity.class);
		restoreIntent.putExtra(BaseToolbarActivity.KEY_THEME, kinRecoveryTheme.name());
		startForResult(restoreIntent, REQ_CODE_RESTORE);
	}

	private void startForResult(@NonNull final Intent intent, final int reqCode) {
		activity.startActivityForResult(intent, reqCode);
		activity.overridePendingTransition(R.anim.kinrecovery_slide_in_right, R.anim.kinrecovery_slide_out_left);
	}
}
