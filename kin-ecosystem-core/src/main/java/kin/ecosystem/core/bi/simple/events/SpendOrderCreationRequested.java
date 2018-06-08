
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Client request OrderID for purchase an offer
 * 
 */
public class SpendOrderCreationRequested implements Event {
    // Augmented by script
    public static SpendOrderCreationRequested create(String offerId) {
        return new SpendOrderCreationRequested(EventName.SPEND_ORDER_CREATION_REQUESTED, Store.common, Store.user, offerId);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private SpendOrderCreationRequested.EventName eventName;
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
     * No args constructor for use in serialization
     * 
     */
    public SpendOrderCreationRequested() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param offerId
     * @param user
     */
    public SpendOrderCreationRequested(SpendOrderCreationRequested.EventName eventName, Common common, User user, String offerId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.offerId = offerId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public SpendOrderCreationRequested.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(SpendOrderCreationRequested.EventName eventName) {
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

    public enum EventName {

        @SerializedName("spend_order_creation_requested")
        SPEND_ORDER_CREATION_REQUESTED("spend_order_creation_requested");
        private final String value;
        private final static Map<String, SpendOrderCreationRequested.EventName> CONSTANTS = new HashMap<String, SpendOrderCreationRequested.EventName>();

        static {
            for (SpendOrderCreationRequested.EventName c: values()) {
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

        public static SpendOrderCreationRequested.EventName fromValue(String value) {
            SpendOrderCreationRequested.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
