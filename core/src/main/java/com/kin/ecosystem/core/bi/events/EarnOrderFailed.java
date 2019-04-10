
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Client reports failure to complete earn order
 * 
 */
public class EarnOrderFailed implements Event {
    public static final String EVENT_NAME = "earn_order_failed";
    public static final String EVENT_TYPE = "log";

    // Augmented by script
    public static EarnOrderFailed create(String errorReason, String offerId, String orderId, EarnOrderFailed.Origin origin) {
        return new EarnOrderFailed(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            errorReason,
            offerId,
            orderId,
            origin);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private String eventName = EVENT_NAME;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_type")
    @Expose
    private String eventType = EVENT_TYPE;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("common")
    @Expose
    private Common common;
    /**
     * common user properties
     * (Required)
     * 
     */
    @SerializedName("user")
    @Expose
    private User user;
    /**
     * common properties for all client events
     * (Required)
     * 
     */
    @SerializedName("client")
    @Expose
    private Client client;
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
     * 
     * (Required)
     * 
     */
    @SerializedName("origin")
    @Expose
    private EarnOrderFailed.Origin origin;

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
     * @param origin

     * @param client
     * @param offerId

     * @param user
     */
    public EarnOrderFailed(Common common, User user, Client client, String errorReason, String offerId, String orderId, EarnOrderFailed.Origin origin) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.errorReason = errorReason;
        this.offerId = offerId;
        this.orderId = orderId;
        this.origin = origin;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
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
     * common user properties
     * (Required)
     * 
     */
    public User getUser() {
        return user;
    }

    /**
     * common user properties
     * (Required)
     * 
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public Client getClient() {
        return client;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public void setClient(Client client) {
        this.client = client;
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

    /**
     * 
     * (Required)
     * 
     */
    public EarnOrderFailed.Origin getOrigin() {
        return origin;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOrigin(EarnOrderFailed.Origin origin) {
        this.origin = origin;
    }

    public enum Origin {

        @SerializedName("marketplace")
        MARKETPLACE("marketplace"),
        @SerializedName("external")
        EXTERNAL("external");
        private final String value;
        private final static Map<String, EarnOrderFailed.Origin> CONSTANTS = new HashMap<String, EarnOrderFailed.Origin>();

        static {
            for (EarnOrderFailed.Origin c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Origin(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static EarnOrderFailed.Origin fromValue(String value) {
            EarnOrderFailed.Origin constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
