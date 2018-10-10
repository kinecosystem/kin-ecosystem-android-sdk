package com.kin.ecosystem.core.data.blockchain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.backup.KeyStoreProvider;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.model.Balance;
import java.math.BigDecimal;
import kin.core.KinAccount;

public interface BlockchainSource {

    /**
     * Getting the current account.
     */
    @Nullable
    KinAccount getKinAccount();

    /**
     *
     * @param appID - appID - will be included in the memo for each transaction.
     */
    void setAppID(String appID);

    /**
     * Send transaction to the network
     * @param publicAddress the recipient address
     * @param amount the amount to send
     * @param orderID the orderID to be included in the memo of the transaction
     */
    void sendTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount, @NonNull String orderID, @NonNull String offerID);

    /**
     * @return the cached balance.
     */
    Balance getBalance();

    /**
     * Get balance from network
     * @param callback
     */
    void getBalance(@Nullable final KinCallback<Balance> callback);

    /**
     * Add balance observer in order to start receive balance updates
     * @param observer
     */
    void addBalanceObserver(@NonNull final Observer<Balance> observer);

    /**
     * Add balance observer that will keep a connection on account balance updates from the blockchain network.
     * @param observer
     */
    void addBalanceObserverAndStartListen(@NonNull final Observer<Balance> observer);

    /**
     * Remove the balance observer in order to stop receiving balance updates.
     * @param observer
     */
    void removeBalanceObserver(@NonNull final Observer<Balance> observer);

    /**
     * Remove the balance observer, and close the connection if no other observers.
     * @param observer
     */
    void removeBalanceObserverAndStopListen(@NonNull final Observer<Balance> observer);

    /**
     * @return the public address of the initiated account
     */
    String getPublicAddress();

    /**
     * Add {@link Payment} completed observer.
     * @param observer
     */
    void addPaymentObservable(Observer<Payment> observer);

    /**
     * Remove the payment observer to stop listening for completed payments.
     * @param observer
     */
    void removePaymentObserver(Observer<Payment> observer);

    /**
     * Create trustline polling call, so it will try few time before failure.
     * @param callback
     */
    void createTrustLine(@NonNull final KinCallback<Void> callback);

    /**
     * Creates the {@link KeyStoreProvider} to use in backup and restore flow.
     * @return {@link KeyStoreProvider}
     */
    KeyStoreProvider getKeyStoreProvider();

    interface Local {

        int getBalance();

        void setBalance(int balance);
    }
}
