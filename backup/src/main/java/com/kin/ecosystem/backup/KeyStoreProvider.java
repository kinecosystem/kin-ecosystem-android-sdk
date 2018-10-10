package com.kin.ecosystem.backup;

import android.support.annotation.NonNull;
import com.kin.ecosystem.backup.exception.BackupException;

public interface KeyStoreProvider {

	String exportAccount(@NonNull final String password) throws BackupException;

	int importAccount(@NonNull final String keyStore, @NonNull final String password) throws BackupException;

	boolean validatePassword(@NonNull final String password);
}
