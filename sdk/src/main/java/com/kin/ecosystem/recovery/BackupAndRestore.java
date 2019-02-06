package com.kin.ecosystem.recovery;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.common.exception.ClientException;

public interface BackupAndRestore {

	void backupFlow() throws ClientException;

	void restoreFlow() throws ClientException;

	void registerBackupCallback(@NonNull final BackupAndRestoreCallback backupCallback);

	void registerRestoreCallback(@NonNull final BackupAndRestoreCallback restoreCallback);

	void release();

	void onActivityResult(int requestCode, int resultCode, Intent data);
}
