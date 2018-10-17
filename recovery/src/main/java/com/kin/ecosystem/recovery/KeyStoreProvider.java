package com.kin.ecosystem.recovery;

import android.support.annotation.NonNull;
import com.kin.ecosystem.recovery.exception.BackupException;

public interface KeyStoreProvider {

	/**
	 * Creates an encoded String representing the keyStore for a specific account that is provided in implementation.
	 *
	 * @param password The password with which to encrypt the account.
	 * Should match the implementation of {@link #validatePassword(String)}
	 * @return A JSON representation of the data as a string.
	 */
	String exportAccount(@NonNull final String password) throws BackupException;

	/**
	 * Create an account from the pair of the correct {@param keyStore} and {@param password}.
	 *
	 * @param keyStore The exported JSON-formatted string representing the account.
	 * @param password The password to decrypt the {@param keyStore}
	 */
	int importAccount(@NonNull final String keyStore, @NonNull final String password) throws BackupException;

	/**
	 * Validated whether the password is matches the rules.
	 *
	 * @param password The password to decrypt the {@param keyStore}
	 * @return true if the {@param password} matched the rules, false otherwise
	 */
	boolean validatePassword(@NonNull final String password);
}
