package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * an open order that hasn&#x27;t been submitted yet
 */

public class OpenOrder {
    @SerializedName("id")
    private String id = null;
    @SerializedName("blockchain_data")
    private BlockchainData blockchainData = null;

    public OpenOrder id(String id) {
        this.id = id;
        return this;
    }


    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OpenOrder blockchainData(BlockchainData blockchainData) {
        this.blockchainData = blockchainData;
        return this;
    }


    /**
     * Get blockchainData
     *
     * @return blockchainData
     **/
    public BlockchainData getBlockchainData() {
        return blockchainData;
    }

    public void setBlockchainData(BlockchainData blockchainData) {
        this.blockchainData = blockchainData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OpenOrder openOrder = (OpenOrder) o;
        return Objects.equals(this.id, openOrder.id) &&
                Objects.equals(this.blockchainData, openOrder.blockchainData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, blockchainData);
    }
}



