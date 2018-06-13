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

	private static final String THE_ECOSYSTEM_SERVER_RETURNED_AN_ERROR = "The Ecosystem server returned an error. See underlyingError for details";
	private static final String ECOSYSTEM_SDK_ENCOUNTERED_AN_UNEXPECTED_ERROR = "Ecosystem SDK encountered an unexpected error";
	private static final String THE_OPERATION_TIMED_OUT = "The operation timed out";
	private static final String YOU_DO_NOT_HAVE_ENOUGH_KIN = "You do not have enough Kin to perform this operation";
	private static final String THE_TRANSACTION_OPERATION_FAILED = "The transaction operation failed. This can happen for several reasons. Please see underlyingError for more info";
	private static final String FAILED_TO_CREATE_A_BLOCKCHAIN_WALLET_KEYPAIR = "Failed to create a blockchain wallet keypair";
	private static final String THE_REQUESTED_ACCOUNT_COULD_NOT_BE_FOUND = "The requested account could not be found";
	private static final String FAILED_TO_ACTIVATE_ON_THE_BLOCKCHAIN_NETWORK = "A Wallet was created locally, but failed to activate on the blockchain network";
	private static final String ECOSYSTEM_SDK_IS_NOT_STARTED = "Operation not permitted: Ecosystem SDK is not started";
	private static final String BAD_OR_MISSING_PARAMETERS = "Bad or missing parameters";

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
					exception = new ServiceException(SERVICE_ERROR, THE_ECOSYSTEM_SERVER_RETURNED_AN_ERROR,
						apiException);
					break;
				case 408:
					exception = new ServiceException(TIMEOUT_ERROR, ECOSYSTEM_SDK_ENCOUNTERED_AN_UNEXPECTED_ERROR,
						apiException);
					break;
				case INTERNAL_INCONSISTENCY:
					exception = new ClientException(INTERNAL_INCONSISTENCY, THE_OPERATION_TIMED_OUT, apiException);
					break;
				default:
					exception = getUnknownException(apiException);
					break;
			}
		}
		return exception;
	}

	private static KinEcosystemException getUnknownException(@Nullable Throwable throwable) {
		return new KinEcosystemException(UNKNOWN, ECOSYSTEM_SDK_ENCOUNTERED_AN_UNEXPECTED_ERROR, throwable);
	}

	public static ApiException getTimeoutException() {
		final String errorTitle = "Time out";
		final String errorMsg = "order timed out";
		final int apiCode = 408;
		ApiException apiException = new ApiException(apiCode, errorTitle);
		apiException.setResponseBody(new Error(errorTitle, errorMsg, TIMEOUT_ERROR));
		return apiException;
	}

	public static BlockchainException getBlockchainException(Exception error) {
		final BlockchainException exception;
		if (error instanceof InsufficientKinException) {
			exception = new BlockchainException(INSUFFICIENT_KIN,
				YOU_DO_NOT_HAVE_ENOUGH_KIN, error);
		} else if (error instanceof TransactionFailedException) {
			exception = new BlockchainException(TRANSACTION_FAILED,
				THE_TRANSACTION_OPERATION_FAILED, error);
		} else if (error instanceof CreateAccountException) {
			exception = new BlockchainException(ACCOUNT_CREATION_FAILED, FAILED_TO_CREATE_A_BLOCKCHAIN_WALLET_KEYPAIR,
				error);
		} else if (error instanceof AccountNotFoundException) {
			exception = new BlockchainException(ACCOUNT_NOT_FOUND, THE_REQUESTED_ACCOUNT_COULD_NOT_BE_FOUND, error);
		} else if (error instanceof AccountNotActivatedException) {
			exception = new BlockchainException(ACCOUNT_ACTIVATION_FAILED,
				FAILED_TO_ACTIVATE_ON_THE_BLOCKCHAIN_NETWORK, error);
		} else {
			exception = new BlockchainException(UNKNOWN, "", error);
		}

		return exception;
	}

	public static ClientException getClientException(final int code, @Nullable Exception e) {
		final ClientException exception;
		switch (code) {
			case SDK_NOT_STARTED:
				exception = new ClientException(SDK_NOT_STARTED,
					ECOSYSTEM_SDK_IS_NOT_STARTED, e);
				break;
			case BAD_CONFIGURATION:
				exception = new ClientException(BAD_CONFIGURATION, BAD_OR_MISSING_PARAMETERS, e);
				break;
			case INTERNAL_INCONSISTENCY:
			default:
				exception = new ClientException(INTERNAL_INCONSISTENCY, ECOSYSTEM_SDK_ENCOUNTERED_AN_UNEXPECTED_ERROR,
					e);
		}

		return exception;
	}
}
