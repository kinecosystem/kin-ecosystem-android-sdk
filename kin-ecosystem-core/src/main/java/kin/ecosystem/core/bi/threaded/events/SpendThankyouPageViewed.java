
package kin.ecosystem.core.bi.threaded.events;

// Augmented by script
import kin.ecosystem.core.bi.threaded.Event;
import kin.ecosystem.core.bi.threaded.EventLoggerImpl;
import kin.ecosystem.core.bi.threaded.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User views the purchase thank you page
 * 
 */
public class SpendThankyouPageViewed implements Event {
    // Augmented by script
    public static SpendThankyouPageViewed create(Double kinAmount, String offerId, String orderId) {
        return new SpendThankyouPageViewed(
            EventName.SPEND_THANKYOU_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            kinAmount,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(Double kinAmount, String offerId, String orderId) {
        final SpendThankyouPageViewed event = new SpendThankyouPageViewed(
            EventName.SPEND_THANKYOU_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
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
    private SpendThankyouPageViewed.EventName eventName;
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
    public SpendThankyouPageViewed() {
    }

    /**
     * 
     * @param common
     * @param orderId
     * @param eventName
     * @param offerId
     * @param kinAmount
     * @param user
     */
    public SpendThankyouPageViewed(SpendThankyouPageViewed.EventName eventName, Common common, User user, Double kinAmount, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.kinAmount = kinAmount;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public SpendThankyouPageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(SpendThankyouPageViewed.EventName eventName) {
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

        @SerializedName("spend_thankyou_page_viewed")
        SPEND_THANKYOU_PAGE_VIEWED("spend_thankyou_page_viewed");
        private final String value;
        private final static Map<String, SpendThankyouPageViewed.EventName> CONSTANTS = new HashMap<String, SpendThankyouPageViewed.EventName>();

        static {
            for (SpendThankyouPageViewed.EventName c: values()) {
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

        public static SpendThankyouPageViewed.EventName fromValue(String value) {
            SpendThankyouPageViewed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
