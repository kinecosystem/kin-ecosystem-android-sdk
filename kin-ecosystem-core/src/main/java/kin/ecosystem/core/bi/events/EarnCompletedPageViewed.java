
package kin.ecosystem.core.bi.events;

// Augmented by script
import kin.ecosystem.core.bi.Event;
import kin.ecosystem.core.bi.EventLoggerImpl;
import kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Users see completion page of a poll (or other earn completion page)
 * 
 */
public class EarnCompletedPageViewed implements Event {
    // Augmented by script
    public static EarnCompletedPageViewed create(EarnCompletedPageViewed.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        return new EarnCompletedPageViewed(
            EventName.EARN_COMPLETED_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            offerType,
            kinAmount,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(EarnCompletedPageViewed.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        final EarnCompletedPageViewed event = new EarnCompletedPageViewed(
            EventName.EARN_COMPLETED_PAGE_VIEWED,
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
    private EarnCompletedPageViewed.EventName eventName;
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
    private EarnCompletedPageViewed.OfferType offerType;
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
    public EarnCompletedPageViewed() {
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
    public EarnCompletedPageViewed(EarnCompletedPageViewed.EventName eventName, Common common, User user, EarnCompletedPageViewed.OfferType offerType, Double kinAmount, String offerId, String orderId) {
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
    public EarnCompletedPageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(EarnCompletedPageViewed.EventName eventName) {
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
    public EarnCompletedPageViewed.OfferType getOfferType() {
        return offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferType(EarnCompletedPageViewed.OfferType offerType) {
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

        @SerializedName("earn_completed_page_viewed")
        EARN_COMPLETED_PAGE_VIEWED("earn_completed_page_viewed");
        private final String value;
        private final static Map<String, EarnCompletedPageViewed.EventName> CONSTANTS = new HashMap<String, EarnCompletedPageViewed.EventName>();

        static {
            for (EarnCompletedPageViewed.EventName c: values()) {
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

        public static EarnCompletedPageViewed.EventName fromValue(String value) {
            EarnCompletedPageViewed.EventName constant = CONSTANTS.get(value);
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
        private final static Map<String, EarnCompletedPageViewed.OfferType> CONSTANTS = new HashMap<String, EarnCompletedPageViewed.OfferType>();

        static {
            for (EarnCompletedPageViewed.OfferType c: values()) {
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

        public static EarnCompletedPageViewed.OfferType fromValue(String value) {
            EarnCompletedPageViewed.OfferType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
