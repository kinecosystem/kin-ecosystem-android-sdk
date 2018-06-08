
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Server fails to create earn transaction on blockchain
 * 
 */
public class EarnTransactionBroadcastToBlockchainFailed implements Event {
    // Augmented by script
    public static EarnTransactionBroadcastToBlockchainFailed create(String errorReason, String offerId, String orderId) {
        return new EarnTransactionBroadcastToBlockchainFailed(EventName.EARN_TRANSACTION_BROADCAST_TO_BLOCKCHAIN_FAILED, Store.common, Store.user, errorReason, offerId, orderId);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private EarnTransactionBroadcastToBlockchainFailed.EventName eventName;
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
    public EarnTransactionBroadcastToBlockchainFailed() {
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
    public EarnTransactionBroadcastToBlockchainFailed(EarnTransactionBroadcastToBlockchainFailed.EventName eventName, Common common, User user, String errorReason, String offerId, String orderId) {
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
    public EarnTransactionBroadcastToBlockchainFailed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(EarnTransactionBroadcastToBlockchainFailed.EventName eventName) {
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

        @SerializedName("earn_transaction_broadcast_to_blockchain_failed")
        EARN_TRANSACTION_BROADCAST_TO_BLOCKCHAIN_FAILED("earn_transaction_broadcast_to_blockchain_failed");
        private final String value;
        private final static Map<String, EarnTransactionBroadcastToBlockchainFailed.EventName> CONSTANTS = new HashMap<String, EarnTransactionBroadcastToBlockchainFailed.EventName>();

        static {
            for (EarnTransactionBroadcastToBlockchainFailed.EventName c: values()) {
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

        public static EarnTransactionBroadcastToBlockchainFailed.EventName fromValue(String value) {
            EarnTransactionBroadcastToBlockchainFailed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
