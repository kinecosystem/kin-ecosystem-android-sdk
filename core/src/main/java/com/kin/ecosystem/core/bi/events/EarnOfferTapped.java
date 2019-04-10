
package com.kin.ecosystem.core.bi.events;

// Augmented by script

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;
import java.util.HashMap;
import java.util.Map;


/**
 * User taps on earn offer in marketplace page
 * 
 */
public class EarnOfferTapped implements Event {
    public static final String EVENT_NAME = "earn_offer_tapped";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static EarnOfferTapped create(EarnOfferTapped.OfferType offerType, Double kinAmount, String offerId, EarnOfferTapped.Origin origin) {
        return new EarnOfferTapped(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            offerType,
            kinAmount,
            offerId,
            origin);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private String eventName = EVENT_NAME;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_type")
    @Expose
    private String eventType = EVENT_TYPE;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("common")
    @Expose
    private Common common;
    /**
     * common user properties
     * (Required)
     * 
     */
    @SerializedName("user")
    @Expose
    private User user;
    /**
     * common properties for all client events
     * (Required)
     * 
     */
    @SerializedName("client")
    @Expose
    private Client client;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("offer_type")
    @Expose
    private EarnOfferTapped.OfferType offerType;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("kin_amount")
    @Expose
    private Double kinAmount;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("offer_id")
    @Expose
    private String offerId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("origin")
    @Expose
    private EarnOfferTapped.Origin origin;

    /**
     * No args constructor for use in serialization
     * 
     */
    public EarnOfferTapped() {
    }

    /**
     * 
     * @param offerType
     * @param common
     * @param origin

     * @param client
     * @param offerId
     * @param kinAmount

     * @param user
     */
    public EarnOfferTapped(Common common, User user, Client client, EarnOfferTapped.OfferType offerType, Double kinAmount, String offerId, EarnOfferTapped.Origin origin) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.offerType = offerType;
        this.kinAmount = kinAmount;
        this.offerId = offerId;
        this.origin = origin;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public Common getCommon() {
        return common;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public void setCommon(Common common) {
        this.common = common;
    }

    /**
     * common user properties
     * (Required)
     * 
     */
    public User getUser() {
        return user;
    }

    /**
     * common user properties
     * (Required)
     * 
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public Client getClient() {
        return client;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * 
     * (Required)
     * 
     */
    public EarnOfferTapped.OfferType getOfferType() {
        return offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferType(EarnOfferTapped.OfferType offerType) {
        this.offerType = offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Double getKinAmount() {
        return kinAmount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setKinAmount(Double kinAmount) {
        this.kinAmount = kinAmount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public EarnOfferTapped.Origin getOrigin() {
        return origin;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOrigin(EarnOfferTapped.Origin origin) {
        this.origin = origin;
    }

    public enum OfferType {

        @SerializedName("poll")
        POLL("poll"),
        @SerializedName("coupon")
        COUPON("coupon"),
        @SerializedName("quiz")
        QUIZ("quiz"),
        @SerializedName("tutorial")
        TUTORIAL("tutorial"),
        @SerializedName("external")
        EXTERNAL("external");
        private final String value;
        private final static Map<String, EarnOfferTapped.OfferType> CONSTANTS = new HashMap<String, EarnOfferTapped.OfferType>();

        static {
            for (EarnOfferTapped.OfferType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private OfferType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static EarnOfferTapped.OfferType fromValue(String value) {
            EarnOfferTapped.OfferType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Origin {

        @SerializedName("marketplace")
        MARKETPLACE("marketplace"),
        @SerializedName("external")
        EXTERNAL("external");
        private final String value;
        private final static Map<String, EarnOfferTapped.Origin> CONSTANTS = new HashMap<String, EarnOfferTapped.Origin>();

        static {
            for (EarnOfferTapped.Origin c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Origin(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static EarnOfferTapped.Origin fromValue(String value) {
            EarnOfferTapped.Origin constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
