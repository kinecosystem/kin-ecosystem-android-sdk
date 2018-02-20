package com.kin.ecosystem.network.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

/**
 * an order submission of a spend offer
 */
public class SpendSubmission {

    /**
     * Gets or Sets offerType
     */
    @JsonAdapter(OfferTypeEnum.Adapter.class)
    public enum OfferTypeEnum {

        SPENDSUBMISSION("SpendSubmission");

        private String value;

        OfferTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static OfferTypeEnum fromValue(String text) {
            for (OfferTypeEnum b : OfferTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<OfferTypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final OfferTypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public OfferTypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return OfferTypeEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("offer_type")
    private OfferTypeEnum offerType = null;
    @SerializedName("blockchain_data")
    private BlockchainData blockchainData = null;

    public SpendSubmission offerType(OfferTypeEnum offerType) {
        this.offerType = offerType;
        return this;
    }


    /**
     * Get offerType
     *
     * @return offerType
     **/
    public OfferTypeEnum getOfferType() {
        return offerType;
    }

    public void setOfferType(OfferTypeEnum offerType) {
        this.offerType = offerType;
    }

    public SpendSubmission blockchainData(BlockchainData blockchainData) {
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
        SpendSubmission spendSubmission = (SpendSubmission) o;
        return Objects.equals(this.offerType, spendSubmission.offerType) &&
                Objects.equals(this.blockchainData, spendSubmission.blockchainData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offerType, blockchainData);
    }
}



