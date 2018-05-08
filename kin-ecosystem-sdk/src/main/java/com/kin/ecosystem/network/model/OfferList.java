package com.kin.ecosystem.network.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * a list of offers
 */
public class OfferList {

    @SerializedName("offers")
    private List<Offer> offers = null;
    @SerializedName("paging")
    private Paging paging = null;

    public OfferList() {
        this.offers = new ArrayList<>();
    }

    public boolean addAtIndex(final int index, @NonNull final Offer offer) {

        if (this.offers == null) {
            this.offers = new ArrayList<>();
            return this.offers.add(offer);
        } else {
            try {
                this.offers.add(index, offer);
                return true;
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        }
    }

    public OfferList addAll(@NonNull final OfferList offerList) {
        List<Offer> offers = offerList.getOffers();
        if (offers != null) {
            this.offers.addAll(size(), offers);
        }

        return this;
    }

    private int size() {
        if (this.offers != null) {
            return this.offers.size();
        }
        return  -1;
    }

    public Offer getOfferByID(String offerID) {
        if (this.offers != null) {
            for (Offer offer : this.offers) {
                if (offer.getId().equals(offerID)) {
                    return offer;
                }
            }
        }
        return null;
    }

    public boolean remove(Offer offer) {
        if (this.offers != null) {
            return this.offers.remove(offer);
        }
        return false;
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
        return this.offers.equals(offerList.offers) &&
            this.paging.equals(offerList.paging);
    }

    @Override
    public int hashCode() {
        return offers.hashCode() + paging.hashCode();
    }
}



