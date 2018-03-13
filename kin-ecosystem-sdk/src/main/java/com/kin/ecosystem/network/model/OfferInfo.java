package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

public class OfferInfo {

    @SerializedName("image")
    private String image;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("amount")
    private int amount;

    @SerializedName("confirmation")
    private Confirmation confirmation;

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public class Confirmation {

        @SerializedName("image")
        private String image;

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;


        public String getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}
