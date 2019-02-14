package com.kin.ecosystem.core.data.blockchain;

import static com.kin.ecosystem.recovery.exception.BackupException.CODE_BACKUP_FAILED;
import static com.kin.ecosystem.recovery.exception.BackupException.CODE_RESTORE_FAILED;
import static com.kin.ecosystem.recovery.exception.BackupException.CODE_RESTORE_INVALID_KEYSTORE_FORMAT;
import static com.kin.ecosystem.recovery.exception.BackupException.CODE_UNEXPECTED;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.core.util.Validator;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import com.kin.ecosystem.recovery.exception.BackupException;
import java.util.regex.Pattern;
import kin.sdk.migration.common.exception.CorruptedDataException;
import kin.sdk.migration.common.exception.CreateAccountException;
import kin.sdk.migration.common.exception.CryptoException;
import kin.sdk.migration.common.interfaces.IKinAccount;
import kin.sdk.migration.common.interfaces.IKinClient;

public class KeyStoreProviderImpl implements KeyStoreProvider {

	@NonNull
	private final IKinClient kinClient;
	@Nullable
	private final IKinAccount kinAccount;
	private final Pattern pattern;

	KeyStoreProviderImpl(@NonNull final IKinClient kinClient, @NonNull final IKinAccount kinAccount) {
		this.kinClient = kinClient;
		this.kinAccount = kinAccount;
		this.pattern = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]])(?!.*[^a-zA-Z0-9!@#$%^&*()_+{}\\[\\]])(.{9,})$");
	}

	@Override
	public String exportAccount(@NonNull final String password) throws BackupException {
		if (kinAccount != null) {
			try {
				return kinAccount.export(password);
			}
			catch (Exception e) {
				if (e instanceof CryptoException) {
					throw new BackupException(CODE_BACKUP_FAILED, "Could not export account see underlying exception", e);
				}

				throw new BackupException(CODE_BACKUP_FAILED, e.getMessage(), e);
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
			IKinAccount importedAccount = kinClient.importAccount(keystore, password);
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

		}
		catch (CryptoException e) {
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
		return pattern.matcher(password).matches();
	}
}
