package com.kin.ecosystem.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * a list of offers
 */
public class OfferList {
    @SerializedName("offers")
    private List<Offer> offers = null;
    @SerializedName("paging")
    private Paging paging = null;

    public OfferList offers(List<Offer> offers) {
        this.offers = offers;
        return this;
    }

    public OfferList addOffersItem(Offer offersItem) {

        if (this.offers == null) {
            this.offers = new ArrayList<Offer>();
        }

        this.offers.add(offersItem);
        return this;
    }

    /**
     * Get offers
     *
     * @return offers
     **/
    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public OfferList paging(Paging paging) {
        this.paging = paging;
        return this;
    }


    /**
     * Get paging
     *
     * @return paging
     **/
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OfferList offerList = (OfferList) o;
        return Objects.equals(this.offers, offerList.offers) &&
                Objects.equals(this.paging, offerList.paging);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offers, paging);
    }
}



