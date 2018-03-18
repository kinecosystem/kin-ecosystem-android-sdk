package com.kin.ecosystem.data.blockchain;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.Payment;
import java.math.BigDecimal;

public interface IBlockchainSource {

    /**
     * Send transaction to the network
     * @param publicAddress the recipient address
     * @param amount the amount to send
     * @param orderID the orderID to be included in the memo of the transaction
     */
    void sendTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount, @NonNull String orderID);

    /**
     * @return balance
     */
    void getBalance(@NonNull final Callback<Integer> callback);

    /**
     * Add balance observer in order to listen for updates
     * @param observer
     */
    void addBalanceObserver(@NonNull final Observer<Integer> observer);

    /**
     * Remove the balance observer in order to stop listening for balance updates.
     * @param observer
     */
    void removeBalanceObserver(@NonNull final Observer<Integer> observer);

    /**
     * Create the initiated account trustline with Kin Asset
     */
    void createTrustLine();

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
}
