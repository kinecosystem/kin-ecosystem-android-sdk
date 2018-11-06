package com.kin.ecosystem.core.data.blockchain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import java.math.BigDecimal;
import kin.core.KinAccount;

public interface BlockchainSource {

	/**
	 * Getting the current account.
	 */
	@Nullable
	KinAccount getKinAccount();

	/**
	 * @param appID - appID - will be included in the memo for each transaction.
	 */
	void setAppID(String appID);

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
	 * Add balance observer in order to start receive balance updates
	 */
	void addBalanceObserver(@NonNull final Observer<Balance> observer);

	/**
	 * Add balance observer that will keep a connection on account balance updates from the blockchain network.
	 */
	void addBalanceObserverAndStartListen(@NonNull final Observer<Balance> observer);

	/**
	 * Remove the balance observer in order to stop receiving balance updates.
	 */
	void removeBalanceObserver(@NonNull final Observer<Balance> observer);

	/**
	 * Remove the balance observer, and close the connection if no other observers.
	 */
	void removeBalanceObserverAndStopListen(@NonNull final Observer<Balance> observer);

	/**
	 * @return the public address of the initiated account
	 */
	String getPublicAddress();

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

	void updateActiveAccount(int accountIndex) throws BlockchainException;

	interface Local {

		int getBalance();

		void setBalance(int balance);

		int getAccountIndex();

		void setAccountIndex(int index);
	}
}
