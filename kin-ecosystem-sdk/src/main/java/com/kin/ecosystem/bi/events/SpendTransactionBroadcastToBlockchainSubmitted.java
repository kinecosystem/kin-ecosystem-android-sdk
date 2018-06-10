
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
 * Clients submits payments to the blockchain
 * 
 */
public class SpendTransactionBroadcastToBlockchainSubmitted implements Event {
    // Augmented by script
    public static SpendTransactionBroadcastToBlockchainSubmitted create(String offerId, String orderId) {
        return new SpendTransactionBroadcastToBlockchainSubmitted(
            EventName.SPEND_TRANSACTION_BROADCAST_TO_BLOCKCHAIN_SUBMITTED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(String offerId, String orderId) {
        final SpendTransactionBroadcastToBlockchainSubmitted event = new SpendTransactionBroadcastToBlockchainSubmitted(
            EventName.SPEND_TRANSACTION_BROADCAST_TO_BLOCKCHAIN_SUBMITTED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
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
    private SpendTransactionBroadcastToBlockchainSubmitted.EventName eventName;
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
    public SpendTransactionBroadcastToBlockchainSubmitted() {
    }

    /**
     * 
     * @param common
     * @param orderId
     * @param eventName
     * @param offerId
     * @param user
     */
    public SpendTransactionBroadcastToBlockchainSubmitted(SpendTransactionBroadcastToBlockchainSubmitted.EventName eventName, Common common, User user, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public SpendTransactionBroadcastToBlockchainSubmitted.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(SpendTransactionBroadcastToBlockchainSubmitted.EventName eventName) {
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

        @SerializedName("spend_transaction_broadcast_to_blockchain_submitted")
        SPEND_TRANSACTION_BROADCAST_TO_BLOCKCHAIN_SUBMITTED("spend_transaction_broadcast_to_blockchain_submitted");
        private final String value;
        private final static Map<String, SpendTransactionBroadcastToBlockchainSubmitted.EventName> CONSTANTS = new HashMap<String, SpendTransactionBroadcastToBlockchainSubmitted.EventName>();

        static {
            for (SpendTransactionBroadcastToBlockchainSubmitted.EventName c: values()) {
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

        public static SpendTransactionBroadcastToBlockchainSubmitted.EventName fromValue(String value) {
            SpendTransactionBroadcastToBlockchainSubmitted.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
