package com.kin.ecosystem.util;

import static com.kin.ecosystem.exception.BlockchainException.ACCOUNT_ACTIVATION_FAILED;
import static com.kin.ecosystem.exception.BlockchainException.ACCOUNT_CREATION_FAILED;
import static com.kin.ecosystem.exception.BlockchainException.ACCOUNT_NOT_FOUND;
import static com.kin.ecosystem.exception.BlockchainException.INSUFFICIENT_KIN;
import static com.kin.ecosystem.exception.BlockchainException.TRANSACTION_FAILED;
import static com.kin.ecosystem.exception.ClientException.BAD_CONFIGURATION;
import static com.kin.ecosystem.exception.ClientException.INTERNAL_INCONSISTENCY;
import static com.kin.ecosystem.exception.ClientException.SDK_NOT_STARTED;
import static com.kin.ecosystem.exception.KinEcosystemException.UNKNOWN;
import static com.kin.ecosystem.exception.ServiceException.SERVICE_ERROR;
import static com.kin.ecosystem.exception.ServiceException.TIMEOUT_ERROR;

import android.support.annotation.Nullable;
import com.kin.ecosystem.exception.BlockchainException;
import com.kin.ecosystem.exception.ClientException;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.exception.ServiceException;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.Error;
import kin.core.exception.AccountNotActivatedException;
import kin.core.exception.AccountNotFoundException;
import kin.core.exception.CreateAccountException;
import kin.core.exception.InsufficientKinException;
import kin.core.exception.TransactionFailedException;

public class ErrorUtil {

	public static KinEcosystemException fromApiException(ApiException apiException) {
		KinEcosystemException exception;
		if (apiException == null) {
			exception = getUnknownException(null);
		} else {
			final int apiCode = apiException.getCode();
			switch (apiCode) {
				case 400:
				case 401:
				case 404:
				case 409:
				case 500:
					exception = new ServiceException(SERVICE_ERROR, "", apiException);
					break;
				case 408:
					exception = new ServiceException(TIMEOUT_ERROR, "", apiException);
					break;
				case INTERNAL_INCONSISTENCY:
					exception = new ClientException(INTERNAL_INCONSISTENCY, "", apiException);
					break;
				default:
					exception = getUnknownException(apiException);
					break;
			}
		}
		return exception;
	}

	private static KinEcosystemException getUnknownException(@Nullable Throwable throwable) {
		return new KinEcosystemException(UNKNOWN, "Could not complete the task", throwable);
	}

	public static ApiException getTimeoutException() {
		final String errorTitle = "timeout";
		final String errorMsg = "order time out";
		final int apiCode = 408;
		ApiException apiException = new ApiException(apiCode, errorTitle);
		apiException.setResponseBody(new Error(errorTitle, errorMsg, TIMEOUT_ERROR));
		return apiException;
	}

	public static BlockchainException getBlockchainException(Exception error) {
		final BlockchainException exception;
		if(error instanceof InsufficientKinException) {
			exception = new BlockchainException(INSUFFICIENT_KIN, "", error);
		}
		else if(error instanceof TransactionFailedException) {
			exception = new BlockchainException(TRANSACTION_FAILED, "" , error);
		}
		else if(error instanceof CreateAccountException) {
			exception = new BlockchainException(ACCOUNT_CREATION_FAILED, "" , error);
		}
		else if(error instanceof AccountNotFoundException) {
			exception = new BlockchainException(ACCOUNT_NOT_FOUND, "" , error);
		}
		else if(error instanceof AccountNotActivatedException) {
			exception = new BlockchainException(ACCOUNT_ACTIVATION_FAILED, "" , error);
		}else {
			exception = new BlockchainException(UNKNOWN, "", error);
		}

		return exception;
	}

	public static ClientException getClientException(final int code, @Nullable Exception e) {
		final ClientException exception;
		switch (code) {
			case SDK_NOT_STARTED:
				exception = new ClientException(SDK_NOT_STARTED, "The SDK not started correctly", e);
				break;
			case BAD_CONFIGURATION:
				exception = new ClientException(BAD_CONFIGURATION, "", e);
				break;
			case INTERNAL_INCONSISTENCY:
			default:
				exception = new ClientException(INTERNAL_INCONSISTENCY, "", e);
		}

		return exception;
	}
}
