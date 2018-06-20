package com.kin.ecosystem.data.model;

import android.support.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Payment object, after sending a blockchain transaction as payment to an order.
 * Determine if the payment succeeded and for which orderID it connected.
 */
public class Payment {

    /**
     * Order id that the transaction is related to.
     */
    private String orderID;

    /**
     * The transaction id on the blockchain, could be null if the transaction failed.
     */
    private @Nullable String transactionID;

    /**
     * The payment amount was sent / received, could be null id transaction failed.
     */
    private @Nullable BigDecimal amount;

    /**
     * Determine if the transaction succeeded or not.
     */
    private boolean isSucceed;

    /**
     * Exception from kin-core:
     */
    private Exception exception;

    public Payment(String orderID, @Nullable String transactionID, @Nullable BigDecimal amount) {
        this.orderID = orderID;
        this.transactionID = transactionID;
        this.amount = amount;
        this.isSucceed = true;
    }

    public Payment(String orderID, boolean isSucceed, Exception error) {
        this.orderID = orderID;
        this.transactionID = null;
        this.amount = null;
        this.isSucceed = isSucceed;
        this.exception = error;
    }

    public String getOrderID() {
        return orderID;
    }

    @Nullable
    public String getTransactionID() {
        return transactionID;
    }

    @Nullable
    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public Exception getException() {
        return exception;
    }

	public boolean isEarn() {
		return amount != null && amount.compareTo(BigDecimal.ZERO) == 1;
	}
}
