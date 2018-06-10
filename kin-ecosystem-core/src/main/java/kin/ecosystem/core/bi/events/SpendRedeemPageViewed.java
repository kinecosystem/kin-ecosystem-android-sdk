
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
 * User views the redeem code page. Source is either "spend flow" or "transaction history" page
 * 
 */
public class SpendRedeemPageViewed implements Event {
    // Augmented by script
    public static SpendRedeemPageViewed create(SpendRedeemPageViewed.RedeemTrigger redeemTrigger, Double kinAmount, String offerId, String orderId) {
        return new SpendRedeemPageViewed(
            EventName.SPEND_REDEEM_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            redeemTrigger,
            kinAmount,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(SpendRedeemPageViewed.RedeemTrigger redeemTrigger, Double kinAmount, String offerId, String orderId) {
        final SpendRedeemPageViewed event = new SpendRedeemPageViewed(
            EventName.SPEND_REDEEM_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            redeemTrigger,
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
    private SpendRedeemPageViewed.EventName eventName;
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
    @SerializedName("redeem_trigger")
    @Expose
    private SpendRedeemPageViewed.RedeemTrigger redeemTrigger;
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
    public SpendRedeemPageViewed() {
    }

    /**
     * 
     * @param common
     * @param orderId
     * @param redeemTrigger
     * @param eventName
     * @param offerId
     * @param kinAmount
     * @param user
     */
    public SpendRedeemPageViewed(SpendRedeemPageViewed.EventName eventName, Common common, User user, SpendRedeemPageViewed.RedeemTrigger redeemTrigger, Double kinAmount, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.redeemTrigger = redeemTrigger;
        this.kinAmount = kinAmount;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public SpendRedeemPageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(SpendRedeemPageViewed.EventName eventName) {
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
    public SpendRedeemPageViewed.RedeemTrigger getRedeemTrigger() {
        return redeemTrigger;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setRedeemTrigger(SpendRedeemPageViewed.RedeemTrigger redeemTrigger) {
        this.redeemTrigger = redeemTrigger;
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

        @SerializedName("spend_redeem_page_viewed")
        SPEND_REDEEM_PAGE_VIEWED("spend_redeem_page_viewed");
        private final String value;
        private final static Map<String, SpendRedeemPageViewed.EventName> CONSTANTS = new HashMap<String, SpendRedeemPageViewed.EventName>();

        static {
            for (SpendRedeemPageViewed.EventName c: values()) {
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

        public static SpendRedeemPageViewed.EventName fromValue(String value) {
            SpendRedeemPageViewed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum RedeemTrigger {

        @SerializedName("user_init")
        USER_INIT("user_init"),
        @SerializedName("system_init")
        SYSTEM_INIT("system_init");
        private final String value;
        private final static Map<String, SpendRedeemPageViewed.RedeemTrigger> CONSTANTS = new HashMap<String, SpendRedeemPageViewed.RedeemTrigger>();

        static {
            for (SpendRedeemPageViewed.RedeemTrigger c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private RedeemTrigger(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static SpendRedeemPageViewed.RedeemTrigger fromValue(String value) {
            SpendRedeemPageViewed.RedeemTrigger constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
