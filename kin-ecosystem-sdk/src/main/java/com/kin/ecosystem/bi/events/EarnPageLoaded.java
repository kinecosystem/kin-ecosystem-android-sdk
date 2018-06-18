
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
 * Generic earn page loaded
 * 
 */
public class EarnPageLoaded implements Event {
    public static final String EVENT_NAME = "earn_page_loaded";
    public static final String EVENT_TYPE = "log";

    // Augmented by script
    public static EarnPageLoaded create(EarnPageLoaded.OfferType offerType) {
        return new EarnPageLoaded(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            offerType);
    }

    // Augmented by script
    public static void fire(EarnPageLoaded.OfferType offerType) {
        final EarnPageLoaded event = new EarnPageLoaded(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            offerType);

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
    private EarnPageLoaded.OfferType offerType;

    /**
     * No args constructor for use in serialization
     * 
     */
    public EarnPageLoaded() {
    }

    /**
     * 
     * @param offerType
     * @param common

     * @param client

     * @param user
     */
    public EarnPageLoaded(Common common, User user, Client client, EarnPageLoaded.OfferType offerType) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.offerType = offerType;
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
    public EarnPageLoaded.OfferType getOfferType() {
        return offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferType(EarnPageLoaded.OfferType offerType) {
        this.offerType = offerType;
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
        private final static Map<String, EarnPageLoaded.OfferType> CONSTANTS = new HashMap<String, EarnPageLoaded.OfferType>();

        static {
            for (EarnPageLoaded.OfferType c: values()) {
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

        public static EarnPageLoaded.OfferType fromValue(String value) {
            EarnPageLoaded.OfferType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
