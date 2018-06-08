
package kin.ecosystem.core.bi.events;

// Augmented by script
import kin.ecosystem.core.bi.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Server submits earn transaction to blockchain and get confirmation
 * 
 */
public class EarnTransactionBroadcastToBlockchainSucceeded {
    // Augmented by script
    public static EarnTransactionBroadcastToBlockchainSucceeded create(String transactionId, String offerId, String orderId) {
        return new EarnTransactionBroadcastToBlockchainSucceeded(EventName.EARN_TRANSACTION_BROADCAST_TO_BLOCKCHAIN_SUCCEEDED, Store.common, Store.user, transactionId, offerId, orderId);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private EarnTransactionBroadcastToBlockchainSucceeded.EventName eventName;
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
    public EarnTransactionBroadcastToBlockchainSucceeded() {
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
    public EarnTransactionBroadcastToBlockchainSucceeded(EarnTransactionBroadcastToBlockchainSucceeded.EventName eventName, Common common, User user, String transactionId, String offerId, String orderId) {
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
    public EarnTransactionBroadcastToBlockchainSucceeded.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(EarnTransactionBroadcastToBlockchainSucceeded.EventName eventName) {
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

        @SerializedName("earn_transaction_broadcast_to_blockchain_succeeded")
        EARN_TRANSACTION_BROADCAST_TO_BLOCKCHAIN_SUCCEEDED("earn_transaction_broadcast_to_blockchain_succeeded");
        private final String value;
        private final static Map<String, EarnTransactionBroadcastToBlockchainSucceeded.EventName> CONSTANTS = new HashMap<String, EarnTransactionBroadcastToBlockchainSucceeded.EventName>();

        static {
            for (EarnTransactionBroadcastToBlockchainSucceeded.EventName c: values()) {
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

        public static EarnTransactionBroadcastToBlockchainSucceeded.EventName fromValue(String value) {
            EarnTransactionBroadcastToBlockchainSucceeded.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
