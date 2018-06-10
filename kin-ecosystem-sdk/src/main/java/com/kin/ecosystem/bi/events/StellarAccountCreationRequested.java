
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
 * Phase 1 - client initate stellar account creation
 * 
 */
public class StellarAccountCreationRequested implements Event {
    // Augmented by script
    public static StellarAccountCreationRequested create() {
        return new StellarAccountCreationRequested(
            EventName.STELLAR_ACCOUNT_CREATION_REQUESTED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final StellarAccountCreationRequested event = new StellarAccountCreationRequested(
            EventName.STELLAR_ACCOUNT_CREATION_REQUESTED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

        EventLoggerImpl.Send(event);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private StellarAccountCreationRequested.EventName eventName;
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
     * No args constructor for use in serialization
     * 
     */
    public StellarAccountCreationRequested() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public StellarAccountCreationRequested(StellarAccountCreationRequested.EventName eventName, Common common, User user) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
    }

    /**
     * 
     * (Required)
     * 
     */
    public StellarAccountCreationRequested.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(StellarAccountCreationRequested.EventName eventName) {
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

    public enum EventName {

        @SerializedName("stellar_account_creation_requested")
        STELLAR_ACCOUNT_CREATION_REQUESTED("stellar_account_creation_requested");
        private final String value;
        private final static Map<String, StellarAccountCreationRequested.EventName> CONSTANTS = new HashMap<String, StellarAccountCreationRequested.EventName>();

        static {
            for (StellarAccountCreationRequested.EventName c: values()) {
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

        public static StellarAccountCreationRequested.EventName fromValue(String value) {
            StellarAccountCreationRequested.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
