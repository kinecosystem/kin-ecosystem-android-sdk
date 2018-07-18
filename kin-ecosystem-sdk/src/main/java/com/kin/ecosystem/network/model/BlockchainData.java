package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 * details taken from a blockchain transaction - all fields optional
 */
public class BlockchainData {
    @SerializedName("transaction_id")
    private String transactionId = null;
    @SerializedName("sender_address")
    private String senderAddress = null;
    @SerializedName("recipient_address")
    private String recipientAddress = null;

    public BlockchainData transactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }


    /**
     * Get transactionId
     *
     * @return transactionId
     **/
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BlockchainData senderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }


    /**
     * Get senderAddress
     *
     * @return senderAddress
     **/
    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public BlockchainData recipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
        return this;
    }


    /**
     * Get recipientAddress
     *
     * @return recipientAddress
     **/
    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockchainData blockchainData = (BlockchainData) o;
        return Objects.equals(this.transactionId, blockchainData.transactionId) &&
                Objects.equals(this.senderAddress, blockchainData.senderAddress) &&
                Objects.equals(this.recipientAddress, blockchainData.recipientAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, senderAddress, recipientAddress);
    }
}



