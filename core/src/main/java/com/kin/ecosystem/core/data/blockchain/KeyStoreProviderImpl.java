package com.kin.ecosystem.core.data.blockchain;

import static com.kin.ecosystem.backup.exception.BackupException.CODE_BACKUP_FAILED;
import static com.kin.ecosystem.backup.exception.BackupException.CODE_RESTORE_FAILED;
import static com.kin.ecosystem.backup.exception.BackupException.CODE_RESTORE_INVALID_KEYSTORE_FORMAT;
import static com.kin.ecosystem.backup.exception.BackupException.CODE_UNEXPECTED;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.backup.KeyStoreProvider;
import com.kin.ecosystem.backup.exception.BackupException;
import com.kin.ecosystem.core.util.Validator;
import java.util.regex.Pattern;
import kin.core.KinAccount;
import kin.core.KinClient;
import kin.core.exception.CorruptedDataException;
import kin.core.exception.CreateAccountException;
import kin.core.exception.CryptoException;

public class KeyStoreProviderImpl implements KeyStoreProvider {

	@NonNull
	private final KinClient kinClient;
	@Nullable
	private final KinAccount kinAccount;

	public KeyStoreProviderImpl(@NonNull final KinClient kinClient, @NonNull final KinAccount kinAccount) {
		this.kinClient = kinClient;
		this.kinAccount = kinAccount;
	}

	@Override
	public String exportAccount(@NonNull final String password) throws BackupException {
		if (kinAccount != null) {
			try {
				return kinAccount.export(password);
			} catch (CryptoException e) {
				throw new BackupException(CODE_BACKUP_FAILED, "Could not export account see underlying exception", e);
			}
		} else {
			throw new BackupException(CODE_UNEXPECTED, "KinAccount could not be null");
		}
	}

	@Override
	public int importAccount(@NonNull final String keystore, @NonNull final String password) throws BackupException {
		Validator.checkNotNull(keystore, "keystore");
		Validator.checkNotNull(keystore, "password");
		try {
			KinAccount importedAccount = kinClient.importAccount(keystore, password);
			int index = -1;
			for (int i = 0; i < kinClient.getAccountCount(); i++) {
				if (importedAccount == kinClient.getAccount(i)) {
					index = i;
				}
			}

			if (index != -1) {
				return index;
			} else {
				throw new BackupException(CODE_UNEXPECTED, "Could not find the imported account");
			}

		} catch (CryptoException e) {
			throw new BackupException(CODE_RESTORE_FAILED, "Could not import the account");
		} catch (CreateAccountException e) {
			throw new BackupException(CODE_RESTORE_FAILED, "Could not create the account");
		} catch (CorruptedDataException e) {
			throw new BackupException(CODE_RESTORE_INVALID_KEYSTORE_FORMAT, "The keystore is invalid - wrong format");
		}
	}

	@Override
	public boolean validatePassword(@NonNull final String password) {
		Validator.checkNotNull(password, "password");
		Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])(?=.*[@#!])(?!.*[^a-zA-Z0-9@#!])(.{9,})$");
		return pattern.matcher(password).matches();
	}
}
