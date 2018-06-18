
package com.kin.ecosystem.bi.events;

// Augmented by script

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kin.ecosystem.bi.Event;
import com.kin.ecosystem.bi.EventLoggerImpl;
import com.kin.ecosystem.bi.EventsStore;
import java.util.HashMap;
import java.util.Map;


/**
 * Users starts an earn offer / Client requests OrderID for earn order
 * 
 */
public class EarnOrderCreationRequested implements Event {
    public static final String EVENT_NAME = "earn_order_creation_requested";
    public static final String EVENT_TYPE = "business";

    // Augmented by script
    public static EarnOrderCreationRequested create(EarnOrderCreationRequested.OfferType offerType, Double kinAmount, String offerId) {
        return new EarnOrderCreationRequested(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            offerType,
            kinAmount,
            offerId);
    }

    // Augmented by script
    public static void fire(EarnOrderCreationRequested.OfferType offerType, Double kinAmount, String offerId) {
        final EarnOrderCreationRequested event = new EarnOrderCreationRequested(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            offerType,
            kinAmount,
            offerId);

        EventLoggerImpl.Send(event);
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
    private EarnOrderCreationRequested.OfferType offerType;
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
     * No args constructor for use in serialization
     * 
     */
    public EarnOrderCreationRequested() {
    }

    /**
     * 
     * @param offerType
     * @param common

     * @param client
     * @param offerId
     * @param kinAmount

     * @param user
     */
    public EarnOrderCreationRequested(Common common, User user, Client client, EarnOrderCreationRequested.OfferType offerType, Double kinAmount, String offerId) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.offerType = offerType;
        this.kinAmount = kinAmount;
        this.offerId = offerId;
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
    public EarnOrderCreationRequested.OfferType getOfferType() {
        return offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferType(EarnOrderCreationRequested.OfferType offerType) {
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
        private final static Map<String, EarnOrderCreationRequested.OfferType> CONSTANTS = new HashMap<String, EarnOrderCreationRequested.OfferType>();

        static {
            for (EarnOrderCreationRequested.OfferType c: values()) {
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

        public static EarnOrderCreationRequested.OfferType fromValue(String value) {
            EarnOrderCreationRequested.OfferType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
