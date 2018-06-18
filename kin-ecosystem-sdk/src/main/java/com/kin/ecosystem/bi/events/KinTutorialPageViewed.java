
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
 * Users see tutorial page
 * 
 */
public class KinTutorialPageViewed implements Event {
    public static final String EVENT_NAME = "kin_tutorial_page_viewed";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static KinTutorialPageViewed create(Integer tutorialStep, KinTutorialPageViewed.OfferType offerType, String offerId, String orderId) {
        return new KinTutorialPageViewed(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            tutorialStep,
            offerType,
            offerId,
            orderId);
    }

    // Augmented by script
    public static void fire(Integer tutorialStep, KinTutorialPageViewed.OfferType offerType, String offerId, String orderId) {
        final KinTutorialPageViewed event = new KinTutorialPageViewed(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            tutorialStep,
            offerType,
            offerId,
            orderId);

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
    @SerializedName("tutorial_step")
    @Expose
    private Integer tutorialStep;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("offer_type")
    @Expose
    private KinTutorialPageViewed.OfferType offerType;
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
    @SerializedName("order_id")
    @Expose
    private String orderId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public KinTutorialPageViewed() {
    }

    /**
     * 
     * @param tutorialStep
     * @param offerType
     * @param common
     * @param orderId

     * @param client
     * @param offerId

     * @param user
     */
    public KinTutorialPageViewed(Common common, User user, Client client, Integer tutorialStep, KinTutorialPageViewed.OfferType offerType, String offerId, String orderId) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.tutorialStep = tutorialStep;
        this.offerType = offerType;
        this.offerId = offerId;
        this.orderId = orderId;
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
    public Integer getTutorialStep() {
        return tutorialStep;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTutorialStep(Integer tutorialStep) {
        this.tutorialStep = tutorialStep;
    }

    /**
     * 
     * (Required)
     * 
     */
    public KinTutorialPageViewed.OfferType getOfferType() {
        return offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferType(KinTutorialPageViewed.OfferType offerType) {
        this.offerType = offerType;
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
    public String getOrderId() {
        return orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
        private final static Map<String, KinTutorialPageViewed.OfferType> CONSTANTS = new HashMap<String, KinTutorialPageViewed.OfferType>();

        static {
            for (KinTutorialPageViewed.OfferType c: values()) {
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

        public static KinTutorialPageViewed.OfferType fromValue(String value) {
            KinTutorialPageViewed.OfferType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
