package com.kin.ecosystem.core.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

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
		return Objects.equals(this.transaction, payload.transaction);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transaction);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class SpendOrderPayload {\n");

		sb.append("    transaction: ").append(toIndentedString(transaction)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
