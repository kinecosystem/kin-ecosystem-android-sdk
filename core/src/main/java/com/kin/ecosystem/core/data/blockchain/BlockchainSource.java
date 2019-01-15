package com.kin.ecosystem.core.data.blockchain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import kin.core.KinAccount;

public interface BlockchainSource {

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
	KinAccount getKinAccount();

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

	interface Local {

		int getBalance();

		void setBalance(int balance);

		int getAccountIndex();

		@Nullable
		String getLastWalletAddress(String kinUserId);

		void setActiveUserWallet(String kinUserId, String publicAddress);

		void removeAccountIndexKey();

		void clearCachedBalance();

		boolean getIsMigrated();

		void setDidMigrate();
	}
}
