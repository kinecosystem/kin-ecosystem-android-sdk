
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
 * Kin balance updated successfully due to a newly confirmed transaction
 * 
 */
public class KinBalanceUpdated implements Event {
    public static final String EVENT_NAME = "kin_balance_updated";
    public static final String EVENT_TYPE = "business";

    // Augmented by script
    public static void fire(Double previousBalance) {
        final KinBalanceUpdated event = new KinBalanceUpdated(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            previousBalance);

        EventLoggerImpl.Send(event);
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
    @SerializedName("previous_balance")
    @Expose
    private Double previousBalance;

    /**
     * No args constructor for use in serialization
     * 
     */
    public KinBalanceUpdated() {
    }

    /**
     * 
     * @param common


     * @param user
     * @param previousBalance
     */
    public KinBalanceUpdated(Common common, User user, Double previousBalance) {
        super();
        this.common = common;
        this.user = user;
        this.previousBalance = previousBalance;
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
    public Double getPreviousBalance() {
        return previousBalance;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPreviousBalance(Double previousBalance) {
        this.previousBalance = previousBalance;
    }

}
