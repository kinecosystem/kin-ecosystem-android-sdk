
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
 * Server tracks the OrderID on the blockchain 
 * 
 */
public class SpendOrderPaymentConfirmed implements Event {
    // Augmented by script
    public static SpendOrderPaymentConfirmed create(String transactionId, String offerId, String orderId) {
        return new SpendOrderPaymentConfirmed(
            EventName.SPEND_ORDER_PAYMENT_CONFIRMED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            transactionId,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(String transactionId, String offerId, String orderId) {
        final SpendOrderPaymentConfirmed event = new SpendOrderPaymentConfirmed(
            EventName.SPEND_ORDER_PAYMENT_CONFIRMED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            transactionId,
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
    private SpendOrderPaymentConfirmed.EventName eventName;
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
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
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
    public SpendOrderPaymentConfirmed() {
    }

    /**
     * 
     * @param common
     * @param orderId
     * @param eventName
     * @param offerId
     * @param user
     * @param transactionId
     */
    public SpendOrderPaymentConfirmed(SpendOrderPaymentConfirmed.EventName eventName, Common common, User user, String transactionId, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.transactionId = transactionId;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public SpendOrderPaymentConfirmed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(SpendOrderPaymentConfirmed.EventName eventName) {
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
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

        @SerializedName("spend_order_payment_confirmed")
        SPEND_ORDER_PAYMENT_CONFIRMED("spend_order_payment_confirmed");
        private final String value;
        private final static Map<String, SpendOrderPaymentConfirmed.EventName> CONSTANTS = new HashMap<String, SpendOrderPaymentConfirmed.EventName>();

        static {
            for (SpendOrderPaymentConfirmed.EventName c: values()) {
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

        public static SpendOrderPaymentConfirmed.EventName fromValue(String value) {
            SpendOrderPaymentConfirmed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
