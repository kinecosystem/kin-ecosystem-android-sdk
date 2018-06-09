
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.simple.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Client fails to create earn order
 * 
 */
public class EarnOrderCreationFailed implements Event {
    // Augmented by script
    public static EarnOrderCreationFailed create(String errorReason, String offerId, String orderId) {
        return new EarnOrderCreationFailed(EventName.EARN_ORDER_CREATION_FAILED, Store.common, Store.user, errorReason, offerId, orderId);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private EarnOrderCreationFailed.EventName eventName;
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
    public EarnOrderCreationFailed() {
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
    public EarnOrderCreationFailed(EarnOrderCreationFailed.EventName eventName, Common common, User user, String errorReason, String offerId, String orderId) {
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
    public EarnOrderCreationFailed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(EarnOrderCreationFailed.EventName eventName) {
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

        @SerializedName("earn_order_creation_failed")
        EARN_ORDER_CREATION_FAILED("earn_order_creation_failed");
        private final String value;
        private final static Map<String, EarnOrderCreationFailed.EventName> CONSTANTS = new HashMap<String, EarnOrderCreationFailed.EventName>();

        static {
            for (EarnOrderCreationFailed.EventName c: values()) {
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

        public static EarnOrderCreationFailed.EventName fromValue(String value) {
            EarnOrderCreationFailed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
