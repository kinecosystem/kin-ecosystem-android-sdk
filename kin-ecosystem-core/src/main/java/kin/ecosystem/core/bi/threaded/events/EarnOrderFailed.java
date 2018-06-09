
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
 * Client reports failure to complete earn order
 * 
 */
public class EarnOrderFailed implements Event {
    // Augmented by script
    public static EarnOrderFailed create(String errorReason, String offerId, String orderId) {
        return new EarnOrderFailed(
            EventName.EARN_ORDER_FAILED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            errorReason,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(String errorReason, String offerId, String orderId) {
        final EarnOrderFailed event = new EarnOrderFailed(
            EventName.EARN_ORDER_FAILED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            errorReason,
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
    private EarnOrderFailed.EventName eventName;
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
    @SerializedName("error_reason")
    @Expose
    private String errorReason;
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
    public EarnOrderFailed() {
    }

    /**
     * 
     * @param common
     * @param orderId
     * @param errorReason
     * @param eventName
     * @param offerId
     * @param user
     */
    public EarnOrderFailed(EarnOrderFailed.EventName eventName, Common common, User user, String errorReason, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.errorReason = errorReason;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public EarnOrderFailed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(EarnOrderFailed.EventName eventName) {
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
    public String getErrorReason() {
        return errorReason;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
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

        @SerializedName("earn_order_failed")
        EARN_ORDER_FAILED("earn_order_failed");
        private final String value;
        private final static Map<String, EarnOrderFailed.EventName> CONSTANTS = new HashMap<String, EarnOrderFailed.EventName>();

        static {
            for (EarnOrderFailed.EventName c: values()) {
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

        public static EarnOrderFailed.EventName fromValue(String value) {
            EarnOrderFailed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
