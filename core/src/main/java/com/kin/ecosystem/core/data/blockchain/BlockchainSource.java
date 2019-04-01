package com.kin.ecosystem.core.data.blockchain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.MigrationInfo;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import java.math.BigDecimal;
import kin.sdk.migration.MigrationManager;
import kin.sdk.migration.common.KinSdkVersion;
import kin.sdk.migration.common.exception.OperationFailedException;
import kin.sdk.migration.common.interfaces.IKinAccount;

public interface BlockchainSource {

	/**
	 * Set the migration manager
	 */
	void setMigrationManager(@NonNull final MigrationManager migrationManager);

	/**
	 * Create account if there is no accounts in local
	 * @param kinUserId the logged in account
	 * @throws BlockchainException could not load the account, or could not create a new account.
	 */
	void loadAccount(String kinUserId) throws BlockchainException;

	/**
	 * Getting the current account.
	 */
	@Nullable
	IKinAccount getKinAccount();

	/**
	 * Start a polling call to account's balance to check if account was created.
	 * @param callback - onResponse if account was created, otherwise onFailure will be triggered.
	 */
	void isAccountCreated(KinCallback<Void> callback);

	interface SignTransactionListener {
		void onTransactionSigned(@NonNull String transaction);
	}

	/**
	 * Only signs the transaction, without sending it
	 *
	 * @param publicAddress the recipient address
	 * @param amount the amount to send
	 * @param orderID the orderID to be included in the memo of the transaction
	 * @param offerID the offerID of the order
	 * @param listener to be informed when a transaction is signed and ready
	 */
	void signTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount, @NonNull String orderID,
		@NonNull String offerID, @NonNull SignTransactionListener listener) throws OperationFailedException;

	/**
	 * Send transaction to the network
	 *
	 * @param publicAddress the recipient address
	 * @param amount the amount to send
	 * @param orderID the orderID to be included in the memo of the transaction
	 */
	void sendTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount, @NonNull String orderID,
		@NonNull String offerID);

	/**
	 * @return the cached balance.
	 */
	Balance getBalance();

	/**
	 * Get balance from network
	 */
	void getBalance(@Nullable final KinCallback<Balance> callback);

	/**
	 * Get balance from network
	 */
	Balance getBalanceSync() throws ClientException, BlockchainException;

	/**
	 * Reconnect the balance connection, due to connection lose.
	 */
	void reconnectBalanceConnection();

	/**
	 * Add balance observer in order to start receive balance updates
	 * @param startSSE true will keep a connection on account balance updates from the blockchain network
	 */
	void addBalanceObserver(@NonNull final Observer<Balance> observer, boolean startSSE);

	/**
	 * Remove the balance observer in order to stop receiving balance updates.
	 * @param stopSSE true will close the connection if no other observers
	 */
	void removeBalanceObserver(@NonNull final Observer<Balance> observer, boolean stopSSE);

	/**
	 * @return the public address of the initiated account
	 */
	String getPublicAddress() throws ClientException, BlockchainException;

	/**
	 * @return the public address of the account with {@param accountIndex}
	 */
	String getPublicAddress(final int accountIndex);

	/**
	 * Add {@link Payment} completed observer.
	 */
	void addPaymentObservable(Observer<Payment> observer);

	/**
	 * Remove the payment observer to stop listening for completed payments.
	 */
	void removePaymentObserver(Observer<Payment> observer);

	/**
	 * Create trustline polling call, so it will try few time before failure.
	 */
	void createTrustLine(@NonNull final KinCallback<Void> callback);

	/**
	 * Creates the {@link KeyStoreProvider} to use in backup and restore flow.
	 *
	 * @return {@link KeyStoreProvider}
	 */
	KeyStoreProvider getKeyStoreProvider();

	/**
	 * Update the activated account
	 * @param accountIndex the new account to load
	 * @return true if switch was succeed
	 */
	boolean updateActiveAccount(int accountIndex);

	void logout();

	KinSdkVersion getBlockchainVersion();

	interface Local {
		int getBalance();

		void setBalance(int balance);

		int getAccountIndex();

		@Nullable
		String getLastWalletAddress(String kinUserId);

		void setActiveUserWallet(String kinUserId, String publicAddress);

		void removeAccountIndexKey();

		void logout();

		boolean getIsMigrated();

		void setDidMigrate();

		KinSdkVersion getBlockchainVersion();

		void setBlockchainVersion(KinSdkVersion version);
	}

	interface Remote {
		KinSdkVersion getBlockchainVersion() throws ApiException; // synced and blocking

		void getBlockchainVersion(@NonNull final Callback<KinSdkVersion, ApiException> callback);

		MigrationInfo getMigrationInfo(String publicAddress)  throws ApiException; // synced and blocking

		void getMigrationInfo(String publicAddress, @NonNull final Callback<MigrationInfo, ApiException> callback);

	}

	interface MigrationProcessListener {
		void onMigrationStart();
		void onMigrationEnd();
		void onMigrationError(BlockchainException error);
	}
}
