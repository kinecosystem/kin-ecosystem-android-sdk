package com.kin.ecosystem.core.network.model;

import com.google.gson.annotations.SerializedName;
import com.kin.ecosystem.core.util.StringUtil;

public class SpendOrderPayload {
	@SerializedName("transaction")
	private String transaction = null;

	public SpendOrderPayload transaction(String transaction) {
		this.transaction = transaction;
		return this;
	}

	/**
	 * json encoded payload related to the spend offer
	 *
	 * @return transaction
	 **/
	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SpendOrderPayload payload = (SpendOrderPayload) o;
		return this.transaction.equals(payload.transaction);
	}

	@Override
	public int hashCode() {
		return transaction.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class SpendOrderPayload {\n");

		sb.append("    transaction: ").append(StringUtil.toIndentedString(transaction)).append("\n");
		sb.append("}");
		return sb.toString();
	}
}
