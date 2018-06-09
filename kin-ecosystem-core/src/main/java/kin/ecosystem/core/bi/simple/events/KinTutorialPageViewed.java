
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.simple.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Users see tutorial page
 * 
 */
public class KinTutorialPageViewed implements Event {
    // Augmented by script
    public static KinTutorialPageViewed create(Integer tutorialStep, KinTutorialPageViewed.OfferType offerType, String offerId, String orderId) {
        return new KinTutorialPageViewed(EventName.KIN_TUTORIAL_PAGE_VIEWED, Store.common, Store.user, tutorialStep, offerType, offerId, orderId);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private KinTutorialPageViewed.EventName eventName;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("common")
    @Expose
    private Common common;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("user")
    @Expose
    private User user;
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
     * @param eventName
     * @param offerId
     * @param user
     */
    public KinTutorialPageViewed(KinTutorialPageViewed.EventName eventName, Common common, User user, Integer tutorialStep, KinTutorialPageViewed.OfferType offerType, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
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
    public KinTutorialPageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(KinTutorialPageViewed.EventName eventName) {
        this.eventName = eventName;
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
     * common properties for all events
     * (Required)
     * 
     */
    public User getUser() {
        return user;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public void setUser(User user) {
        this.user = user;
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

    public enum EventName {

        @SerializedName("kin_tutorial_page_viewed")
        KIN_TUTORIAL_PAGE_VIEWED("kin_tutorial_page_viewed");
        private final String value;
        private final static Map<String, KinTutorialPageViewed.EventName> CONSTANTS = new HashMap<String, KinTutorialPageViewed.EventName>();

        static {
            for (KinTutorialPageViewed.EventName c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private EventName(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static KinTutorialPageViewed.EventName fromValue(String value) {
            KinTutorialPageViewed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum OfferType {

        @SerializedName("Video")
        VIDEO("Video"),
        @SerializedName("Poll")
        POLL("Poll"),
        @SerializedName("code purchase")
        CODE_PURCHASE("code purchase"),
        @SerializedName("Tutorial")
        TUTORIAL("Tutorial");
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
