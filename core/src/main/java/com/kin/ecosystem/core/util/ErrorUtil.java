package com.kin.ecosystem.core.util;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.Error;
import kin.core.exception.AccountNotActivatedException;
import kin.core.exception.AccountNotFoundException;
import kin.core.exception.CreateAccountException;
import kin.core.exception.InsufficientKinException;
import kin.core.exception.TransactionFailedException;

public class ErrorUtil {

	// Error messages
	private static final String THE_ECOSYSTEM_SERVER_RETURNED_AN_ERROR = "The Ecosystem server returned an error. See underlying Error for details";
	private static final String ECOSYSTEM_SDK_ENCOUNTERED_AN_UNEXPECTED_ERROR = "Ecosystem SDK encountered an unexpected error";
	private static final String BLOCKCHAIN_ENCOUNTERED_AN_UNEXPECTED_ERROR = "Blockchain encountered an unexpected error";
	private static final String THE_OPERATION_TIMED_OUT = "The operation timed out";
	private static final String YOU_DO_NOT_HAVE_ENOUGH_KIN = "You do not have enough Kin to perform this operation";
	private static final String THE_TRANSACTION_OPERATION_FAILED = "The transaction operation failed. This can happen for several reasons. Please see underlying Error for more info";
	private static final String FAILED_TO_CREATE_A_BLOCKCHAIN_WALLET_KEYPAIR = "Failed to create a blockchain wallet keypair";
	private static final String THE_REQUESTED_ACCOUNT_COULD_NOT_BE_FOUND = "The requested account could not be found";
	private static final String FAILED_TO_ACTIVATE_ON_THE_BLOCKCHAIN_NETWORK = "A Wallet was created locally, but failed to activate on the blockchain network";
	private static final String ECOSYSTEM_SDK_IS_NOT_STARTED = "Operation not permitted: Ecosystem SDK is not started, please call Kin.initialize(...) first.";
	private static final String BAD_OR_MISSING_PARAMETERS = "Bad or missing parameters";
	private static final String FAILED_TO_LOAD_ACCOUNT_ON_INDEX = "Failed to load blockchain wallet on index %d";
	private static final String ACCOUNT_IS_NOT_LOGGED_IN = "Account is not logged in, please call Kin.login(...) first.";
	private static final String ACCOUNT_NOT_FOUND = "Account not found";
	private static final String ACCOUNT_HAS_NO_WALLET = "Account has no wallet";


	// Server Error codes
	private static final int ERROR_CODE_BAD_REQUEST = 400;
	private static final int ERROR_CODE_UNAUTHORIZED = 401;
	private static final int ERROR_CODE_NOT_FOUND = 404;
	private static final int ERROR_CODE_REQUEST_TIMEOUT = 408;
	public static final int ERROR_CODE_CONFLICT = 409;
	private static final int ERROR_CODE_INTERNAL_SERVER_ERROR = 500;
	private static final int ERROR_CODE_TRANSACTION_FAILED_ERROR = 700;

	private static final int ERROR_CODE_NO_SUCH_USER = 4046;
	private static final int ERROR_CODE_USER_HAS_NO_WALLET = 4095;
	public static final int ERROR_CODE_EXTERNAL_ORDER_ALREADY_COMPLETED = 4091;

	public static KinEcosystemException fromApiException(ApiException apiException) {
		if (apiException == null) {
			return createUnknownServiceException(null);
		} else {
			final int apiCode = apiException.getCode();
			Error error = apiException.getResponseBody();
			switch (apiCode) {
				case ERROR_CODE_BAD_REQUEST:
				case ERROR_CODE_UNAUTHORIZED:
				case ERROR_CODE_NOT_FOUND:
					if (error != null) {
						switch (error.getCode()) {
							case ERROR_CODE_NO_SUCH_USER:
								return new ServiceException(ServiceException.USER_NOT_FOUND, getMessageOrDefault(error, ACCOUNT_NOT_FOUND), apiException);

							case ERROR_CODE_USER_HAS_NO_WALLET:
								return new ServiceException(ServiceException.USER_HAS_NO_WALLET, getMessageOrDefault(error, ACCOUNT_HAS_NO_WALLET), apiException);
						}
					}
				case ERROR_CODE_CONFLICT:
				case ERROR_CODE_INTERNAL_SERVER_ERROR:
				case ERROR_CODE_TRANSACTION_FAILED_ERROR:
					if (error != null) {
						return new ServiceException(ServiceException.SERVICE_ERROR,
							getMessageOrDefault(error, THE_ECOSYSTEM_SERVER_RETURNED_AN_ERROR), apiException);
					}
					return createUnknownServiceException(apiException);
				case ERROR_CODE_REQUEST_TIMEOUT:
					return new ServiceException(ServiceException.TIMEOUT_ERROR, THE_OPERATION_TIMED_OUT, apiException);
				case ClientException.INTERNAL_INCONSISTENCY:
					return new ClientException(ClientException.INTERNAL_INCONSISTENCY, THE_OPERATION_TIMED_OUT,
						apiException);
				default:
					return createUnknownServiceException(apiException);

			}
		}
	}

	private static KinEcosystemException createUnknownServiceException(@Nullable Throwable throwable) {
		final String msg = getMessage(throwable);
		return new ServiceException(ServiceException.SERVICE_ERROR, msg, throwable);
	}

	private static String getMessageOrDefault(Error error, final String defaultMsg) {
		return error != null && StringUtil.isEmpty(error.getMessage()) ? error.getMessage() : defaultMsg;
	}

	private static String getMessage(Throwable throwable) {
		return (throwable != null && throwable.getMessage() != null) ? throwable.getMessage()
			: getCauseOrDefault(throwable);
	}

	private static String getCauseOrDefault(Throwable throwable) {
		return (throwable != null && throwable.getCause() != null && throwable.getCause().getMessage() != null)
			? throwable.getCause().getMessage() : ECOSYSTEM_SDK_ENCOUNTERED_AN_UNEXPECTED_ERROR;
	}


	public static ApiException createOrderTimeoutException() {
		final String errorTitle = "Time out";
		final String errorMsg = "order timed out";
		ApiException apiException = new ApiException(ERROR_CODE_REQUEST_TIMEOUT, errorTitle);
		apiException.setResponseBody(new Error(errorTitle, errorMsg, ServiceException.TIMEOUT_ERROR));
		return apiException;
	}

	public static BlockchainException getBlockchainException(Exception error) {
		final BlockchainException exception;
		if (error instanceof InsufficientKinException) {
			exception = new BlockchainException(BlockchainException.INSUFFICIENT_KIN, YOU_DO_NOT_HAVE_ENOUGH_KIN,
				error);
		} else if (error instanceof TransactionFailedException) {
			exception = new BlockchainException(BlockchainException.TRANSACTION_FAILED,
				THE_TRANSACTION_OPERATION_FAILED, error);
		} else if (error instanceof CreateAccountException) {
			exception = new BlockchainException(BlockchainException.ACCOUNT_CREATION_FAILED,
				FAILED_TO_CREATE_A_BLOCKCHAIN_WALLET_KEYPAIR,
				error);
		} else if (error instanceof AccountNotFoundException) {
			exception = new BlockchainException(BlockchainException.ACCOUNT_NOT_FOUND,
				THE_REQUESTED_ACCOUNT_COULD_NOT_BE_FOUND, error);
		} else if (error instanceof AccountNotActivatedException) {
			exception = new BlockchainException(BlockchainException.ACCOUNT_ACTIVATION_FAILED,
				FAILED_TO_ACTIVATE_ON_THE_BLOCKCHAIN_NETWORK, error);
		} else {
			exception = new BlockchainException(KinEcosystemException.UNKNOWN,
				BLOCKCHAIN_ENCOUNTERED_AN_UNEXPECTED_ERROR, error);
		}

		return exception;
	}

	@SuppressLint("DefaultLocale")
	public static BlockchainException createAccountCannotLoadedException(int accountIndex) {
		return new BlockchainException(BlockchainException.ACCOUNT_LOADING_FAILED,
			String.format(FAILED_TO_LOAD_ACCOUNT_ON_INDEX, accountIndex), null);
	}

	public static ClientException getClientException(final int code, @Nullable Exception e) {
		final ClientException exception;
		switch (code) {
			case ClientException.SDK_NOT_STARTED:
				exception = new ClientException(ClientException.SDK_NOT_STARTED, ECOSYSTEM_SDK_IS_NOT_STARTED, e);
				break;
			case ClientException.BAD_CONFIGURATION:
				exception = new ClientException(ClientException.BAD_CONFIGURATION, BAD_OR_MISSING_PARAMETERS, e);
				break;
			case ClientException.ACCOUNT_NOT_LOGGED_IN:
				exception = new ClientException(ClientException.ACCOUNT_NOT_LOGGED_IN, ACCOUNT_IS_NOT_LOGGED_IN, e);
				break;
			case ClientException.INTERNAL_INCONSISTENCY:
			default:
				exception = new ClientException(ClientException.INTERNAL_INCONSISTENCY,
					ECOSYSTEM_SDK_ENCOUNTERED_AN_UNEXPECTED_ERROR,
					e);
		}

		return exception;
	}
}
