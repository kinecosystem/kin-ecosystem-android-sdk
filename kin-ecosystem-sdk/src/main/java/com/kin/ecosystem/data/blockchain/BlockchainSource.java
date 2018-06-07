package com.kin.ecosystem.data.blockchain;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.BalanceUpdate;
import com.kin.ecosystem.data.model.Payment;
import java.math.BigDecimal;

public interface BlockchainSource {

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
    void sendTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount, @NonNull String orderID);

    /**
     * @return the cached balance.
     */
    BalanceUpdate getBalance();

    /**
     * Get balance from network
     * @param callback
     */
    void getBalance(@NonNull final Callback<BalanceUpdate> callback);

    /**
     * Add balance observer in order to start receive balance updates
     * @param observer
     */
    void addBalanceObserver(@NonNull final Observer<BalanceUpdate> observer);

    /**
     * Add balance observer and start SSE connection on account balance updates from the blockchain network.
     * @param observer
     */
    void addBalanceObserverAndStartListen(@NonNull final Observer<BalanceUpdate> observer);

    /**
     * Remove the balance observer in order to stop receiving balance updates.
     * @param observer
     */
    void removeBalanceObserver(@NonNull final Observer<BalanceUpdate> observer);

    /**
     * Remove the balance observer, and close SSE connection if no other observers.
     * @param observer
     */
    void removeBalanceObserverAndStopListen(@NonNull final Observer<BalanceUpdate> observer);

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

    interface Local {

        int getBalance();

        void setBalance(int balance);

    }
}
