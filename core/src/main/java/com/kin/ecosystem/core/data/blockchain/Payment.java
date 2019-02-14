package com.kin.ecosystem.core.data.blockchain;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;

/**
 * Payment object, after sending a blockchain transaction as payment to an order.
 * Determine if the payment succeeded and for which orderID it connected.
 */
public class Payment {

	public static final int UNKNOWN = 0x00000000;
	public static final int EARN = 0x00000001;
	public static final int SPEND = 0x00000002;

	@IntDef({UNKNOWN, EARN, SPEND})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Type {

	}

	/**
	 * Order id that the transaction is related to.
	 */
	private String orderID;

	/**
	 * The transaction id on the blockchain, could be null if the transaction failed.
	 */
	private @Nullable
	String transactionID;

	/**
	 * The payment amount was sent / received, could be null id transaction failed.
	 */
	private @Nullable
	BigDecimal amount;

	/**
	 * Determine if the transaction succeeded or not.
	 */
	private boolean isSucceed;

	/**
	 * Exception from kin.sdk.migration.common.interfaces.I
	 */
	private Exception exception;

	/**
	 * The {@link Type} of payment: EARN, SPEND or UNKNOWN if the payment failed.
	 */
	private @Type
	int type = UNKNOWN;

	public Payment(String orderID, @Nullable String transactionID, @Nullable BigDecimal amount, @Type int type) {
		this.orderID = orderID;
		this.transactionID = transactionID;
		this.amount = amount;
		this.isSucceed = true;
		this.type = type;
	}

	public Payment(String orderID, boolean isSucceed, Exception exception) {
		this.orderID = orderID;
		this.transactionID = null;
		this.amount = null;
		this.isSucceed = isSucceed;
		this.exception = exception;
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

	public int getType() {
		return type;
	}
}
