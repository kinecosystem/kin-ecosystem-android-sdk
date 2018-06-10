
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
 * User taps on earn offer in marketplace page
 * 
 */
public class EarnOfferTapped implements Event {
    // Augmented by script
    public static EarnOfferTapped create(EarnOfferTapped.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        return new EarnOfferTapped(
            EventName.EARN_OFFER_TAPPED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            offerType,
            kinAmount,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(EarnOfferTapped.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        final EarnOfferTapped event = new EarnOfferTapped(
            EventName.EARN_OFFER_TAPPED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            offerType,
            kinAmount,
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
    private EarnOfferTapped.EventName eventName;
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
    @SerializedName("order_id")
    @Expose
    private String orderId;

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
     * @param orderId
     * @param eventName
     * @param offerId
     * @param kinAmount
     * @param user
     */
    public EarnOfferTapped(EarnOfferTapped.EventName eventName, Common common, User user, EarnOfferTapped.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.offerType = offerType;
        this.kinAmount = kinAmount;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public EarnOfferTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(EarnOfferTapped.EventName eventName) {
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

        @SerializedName("earn_offer_tapped")
        EARN_OFFER_TAPPED("earn_offer_tapped");
        private final String value;
        private final static Map<String, EarnOfferTapped.EventName> CONSTANTS = new HashMap<String, EarnOfferTapped.EventName>();

        static {
            for (EarnOfferTapped.EventName c: values()) {
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

        public static EarnOfferTapped.EventName fromValue(String value) {
            EarnOfferTapped.EventName constant = CONSTANTS.get(value);
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

}
