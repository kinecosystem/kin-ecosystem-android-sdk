
package com.kin.ecosystem.bi.events;

// Augmented by script
import com.kin.ecosystem.bi.Event;
import com.kin.ecosystem.bi.EventLoggerImpl;
import com.kin.ecosystem.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User taps next on tutorial page
 * 
 */
public class KinTutorialButtonTapped implements Event {
    // Augmented by script
    public static KinTutorialButtonTapped create(Integer tutorialStep, KinTutorialButtonTapped.OfferType offerType, String offerId, String orderId) {
        return new KinTutorialButtonTapped(
            EventName.KIN_TUTORIAL_BUTTON_TAPPED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            tutorialStep,
            offerType,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(Integer tutorialStep, KinTutorialButtonTapped.OfferType offerType, String offerId, String orderId) {
        final KinTutorialButtonTapped event = new KinTutorialButtonTapped(
            EventName.KIN_TUTORIAL_BUTTON_TAPPED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
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
    private KinTutorialButtonTapped.EventName eventName;
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
    private KinTutorialButtonTapped.OfferType offerType;
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
    public KinTutorialButtonTapped() {
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
    public KinTutorialButtonTapped(KinTutorialButtonTapped.EventName eventName, Common common, User user, Integer tutorialStep, KinTutorialButtonTapped.OfferType offerType, String offerId, String orderId) {
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
    public KinTutorialButtonTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(KinTutorialButtonTapped.EventName eventName) {
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
    public KinTutorialButtonTapped.OfferType getOfferType() {
        return offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferType(KinTutorialButtonTapped.OfferType offerType) {
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

        @SerializedName("kin_tutorial_button_tapped")
        KIN_TUTORIAL_BUTTON_TAPPED("kin_tutorial_button_tapped");
        private final String value;
        private final static Map<String, KinTutorialButtonTapped.EventName> CONSTANTS = new HashMap<String, KinTutorialButtonTapped.EventName>();

        static {
            for (KinTutorialButtonTapped.EventName c: values()) {
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

        public static KinTutorialButtonTapped.EventName fromValue(String value) {
            KinTutorialButtonTapped.EventName constant = CONSTANTS.get(value);
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
        private final static Map<String, KinTutorialButtonTapped.OfferType> CONSTANTS = new HashMap<String, KinTutorialButtonTapped.OfferType>();

        static {
            for (KinTutorialButtonTapped.OfferType c: values()) {
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

        public static KinTutorialButtonTapped.OfferType fromValue(String value) {
            KinTutorialButtonTapped.OfferType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
